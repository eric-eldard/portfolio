package com.eric_eldard.portfolio.service.auth;

import static com.eric_eldard.portfolio.util.Constants.$;
import static com.eric_eldard.portfolio.util.Constants.JWT_COOKIE_NAME;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.Nullable;
import jakarta.inject.Inject;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;

import com.eric_eldard.portfolio.config.GlobalConfig;
import com.eric_eldard.portfolio.model.auth.JwsAuthToken;
import com.eric_eldard.portfolio.model.user.PortfolioUser;
import com.eric_eldard.portfolio.security.csrf.PortfolioCsrfTokenRepository;
import com.eric_eldard.portfolio.security.exception.InvalidTokenException;
import com.eric_eldard.portfolio.security.filter.JwsFilter;
import com.eric_eldard.portfolio.security.util.JwtUtil;
import com.eric_eldard.portfolio.service.user.PortfolioUserService;
import com.eric_eldard.portfolio.service.user.SecurityContextService;
import com.eric_eldard.portfolio.service.web.CookieService;
import com.eric_eldard.portfolio.util.Constants;

@Service
public class AuthenticationServiceImpl implements AuthenticationService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationServiceImpl.class);

    private static final Date SERVER_START = new Date();

    private final CookieService cookieService;

    private final JwtUtil jwtUtil;

    private final long jwtTtlSeconds;

    private final PortfolioCsrfTokenRepository csrfTokenRepo;

    private final PortfolioUserService userService;

    private final SecurityContextService securityContextService;

    private final Cache<Long, Date> usersRequiringFreshClaims;

    private AuthenticationManager authenticationManager;


    public AuthenticationServiceImpl(@Value("${portfolio.security.jwt.ttl-sec}") long jwtTtlSeconds,
                                     CookieService cookieService,
                                     JwtUtil jwtUtil,
                                     PortfolioCsrfTokenRepository csrfTokenRepo,
                                     PortfolioUserService userService,
                                     SecurityContextService securityContextService
    )
    {
        this.cookieService = cookieService;
        this.jwtTtlSeconds = jwtTtlSeconds;
        this.jwtUtil = jwtUtil;
        this.csrfTokenRepo = csrfTokenRepo;
        this.userService = userService;
        this.securityContextService = securityContextService;
        this.usersRequiringFreshClaims = CacheBuilder.newBuilder()
            .expireAfterWrite(jwtTtlSeconds, TimeUnit.SECONDS)
            .build();
    }


    /**
     * The authentication manager is created in {@link GlobalConfig}, which unfortunately requires an authentication
     * service to instantiate a {@link JwsFilter}. Alas, chicken-and-egg results in this grossness.
     */
    @Lazy
    @Inject
    public void setAuthenticationManager(AuthenticationManager authenticationManager)
    {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication authenticate(String username, String password)
    {
        return authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(username, password)
        );
    }

    @Override
    public String issueToken(PortfolioUser user)
    {
        Date issuedAt = new Date();
        Date expiry = new Date(issuedAt.getTime() + (jwtTtlSeconds * 1_000L));
        return issueToken(user, issuedAt, expiry);
    }

    @Override
    public void setAuthenticationForRequest(String incomingClaims,
                                            HttpServletRequest request,
                                            HttpServletResponse response)
    {
        // We wrap the claims immediately to leverage convenience methods in the token class
        JwsAuthToken incomingToken = new JwsAuthToken(resolveClaims(incomingClaims));

        if (incomingToken.isExpired())
        {
            throw new InvalidTokenException("User [" + incomingToken.username() + "] presented an expired auth token");
        }

        Date dateRefreshRequired = usersRequiringFreshClaims.getIfPresent(incomingToken.userId());

        // - Refresh required for tokens issued prior to app restart
        // - Refresh required if presented token was issued prior to last time a refresh was required for this user
        boolean refreshRequired = incomingToken.serverStart().before(SERVER_START) ||
            dateRefreshRequired != null && incomingToken.issuedAt().before(dateRefreshRequired);

        String claimsToPresent;
        JwsAuthToken tokenToPresent;
        if (refreshRequired)
        {
            claimsToPresent = refreshTokenClaims(incomingToken);
            tokenToPresent = new JwsAuthToken(resolveClaims(claimsToPresent));
        }
        else
        {
            claimsToPresent = incomingClaims;
            tokenToPresent = incomingToken;
        }

        validateAccount(tokenToPresent);

        tokenToPresent.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // Make cookie with expiry matching expiry of auth token
        setAuthTokenCookie(claimsToPresent, tokenToPresent.getSecondsUntilExpiration(), response);

        securityContextService.setAuthentication(tokenToPresent);
    }

    @Override
    public void logUserOut(HttpServletResponse response, @Nullable Authentication authentication)
    {
        response.addCookie(cookieService.makeExpiredCookie(JWT_COOKIE_NAME));
        if (authentication instanceof JwsAuthToken jwsAuthToken)
        {
            csrfTokenRepo.invalidateForUser(jwsAuthToken.userId());
        }
    }

    @Override
    public void requireFreshClaimsForUser(long userId)
    {
        Optional<PortfolioUser> user = userService.findById(userId);
        // Deleted users also go into the require-fresh-lookup cache, so it's possible we won't find them
        Date now = new Date();
        LOGGER.info("Tokens issued to user [{}] prior to {} will require claims refreshing",
            user.isPresent() ? user.get().getUsername() : userId,
            new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(now)
        );
        usersRequiringFreshClaims.put(userId, now);
    }


    private String issueToken(PortfolioUser user, Date issuedAt, Date expiry)
    {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", user.getUsername());
        claims.put("enabled", user.isEnabled());
        claims.put("authorized_until", user.getAuthorizedUntil());
        claims.put("locked_on", user.getLockedOn());
        claims.put(Constants.SERVER_START_CLAIM, SERVER_START);
        // "admin" is a granted authority, so no need to add it as a separate claim

        int index = 1;
        for (GrantedAuthority authority : user.getAuthorities())
        {
            claims.put("GrantedAuthority-" + index++, authority.getAuthority());
        }

        return jwtUtil.buildToken(String.valueOf(user.getId()), claims, issuedAt, expiry);
    }

    private Jws<Claims> resolveClaims(String token)
    {
        try
        {
            return jwtUtil.resolveClaims(token);
        }
        catch (Exception ex)
        {
            throw new InvalidTokenException("User presented a malformed auth token", ex);
        }
    }

    private String refreshTokenClaims(JwsAuthToken originalToken)
    {
        Optional<PortfolioUser> user = userService.findWithAuthoritiesById(originalToken.userId());
        if (user.isEmpty())
        {
            throw new UsernameNotFoundException("No user maps to ID " + originalToken.userId());
        }

        Date originalTokenExpiration = originalToken.getPrincipal().getPayload().getExpiration();
        return issueToken(user.get(), new Date(), originalTokenExpiration);
    }

    private void validateAccount(JwsAuthToken token)
    {
        String username = token.username();

        if (token.accountLocked())
        {
            throw new LockedException($."The account for user [\{username}] is locked for too many failed attempts.");
        }
        else if (token.accountDisabled())
        {
            throw new DisabledException($."The account for user [\{username}] is permanently disabled.");
        }
        else if (token.accountExpired())
        {
            throw new AccountExpiredException($."The account for user [\{username}] has expired.");
        }
    }

    private void setAuthTokenCookie(String token, int maxAge, HttpServletResponse response)
    {
        Cookie tokenCookie = cookieService.makePersistentCookie(JWT_COOKIE_NAME, token, maxAge);
        response.addCookie(tokenCookie);
    }
}