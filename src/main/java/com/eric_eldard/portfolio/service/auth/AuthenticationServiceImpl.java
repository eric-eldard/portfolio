package com.eric_eldard.portfolio.service.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;

import jakarta.inject.Inject;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.eric_eldard.portfolio.config.GlobalConfig;
import com.eric_eldard.portfolio.model.user.PortfolioUser;
import com.eric_eldard.portfolio.security.filter.JwtFilter;
import com.eric_eldard.portfolio.security.util.JwtUtil;
import com.eric_eldard.portfolio.service.user.PortfolioUserService;
import com.eric_eldard.portfolio.service.user.SecurityContextService;
import com.eric_eldard.portfolio.service.web.CookieService;
import com.eric_eldard.portfolio.util.Constants;

/**
 * A facade for specific auth-related functionalities in
 * <ul>
 *     <li>{@link AuthenticationManager}</li>
 *     <li>{@link CookieService}</li>
 *     <li>{@link JwtUtil}</li>
 *     <li>{@link PortfolioUserService}</li>
 *     <li>{@link SecurityContextService}</li>
 * </ul>
 * including some bundled functionality
 */
@Service
public class AuthenticationServiceImpl implements AuthenticationService
{
    private final CookieService cookieService;

    private final JwtUtil jwtUtil;

    private final long jwtTtlMillis;

    private final PortfolioUserService portfolioUserService;

    private final SecurityContextService securityContextService;

    private AuthenticationManager authenticationManager;


    public AuthenticationServiceImpl(@Value("${portfolio.security.jwt.ttl:10080}") long jwtTtlMins,
                                     CookieService cookieService,
                                     JwtUtil jwtUtil,
                                     PortfolioUserService portfolioUserService,
                                     SecurityContextService securityContextService
    )
    {
        this.cookieService = cookieService;
        this.jwtTtlMillis = TimeUnit.MINUTES.toMillis(jwtTtlMins);
        this.jwtUtil = jwtUtil;
        this.portfolioUserService = portfolioUserService;
        this.securityContextService = securityContextService;
    }


    /**
     * The authentication manager is created in {@link GlobalConfig}, which unfortunately requires an authentication
     * service to instantiate a {@link JwtFilter}. Alas, chicken-and-egg results in this grossness.
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
        Date expiry = new Date(issuedAt.getTime() + jwtTtlMillis);
        return issueToken(user, issuedAt, expiry);
    }

    @Override
    public String issueToken(PortfolioUser user, Date issuedAt, Date expiry)
    {
        return jwtUtil.buildToken(String.valueOf(user.getId()), Map.of(), issuedAt, expiry);
    }

    @Override
    public Jws<Claims> resolveClaims(String token)
    {
        return jwtUtil.resolveClaims(token);
    }

    @Override
    public boolean valid(Jws<Claims> signedToken) throws AuthenticationException
    {
        return jwtUtil.validate(signedToken);
    }

    @Override
    public void setAuthentication(long userId, HttpServletRequest request)
    {
        Optional<PortfolioUser> user = portfolioUserService.findWithAuthoritiesById(userId);
        if (user.isEmpty())
        {
            throw new UsernameNotFoundException("No user maps to ID " + userId);
        }
        setAuthentication(user.get(), request);
    }

    @Override
    public void setAuthentication(PortfolioUser user, HttpServletRequest request)
    {
        String username = user.getUsername();
        if (user.isAccountLocked())
        {
            throw new LockedException("The account for user [" + username + "] is locked for too many failed attempts.");
        }
        else if (!user.isEnabled())
        {
            throw new DisabledException("The account for user [" + username + "] is permanently disabled.");
        }
        else if (!user.isAccountNonExpired())
        {
            throw new AccountExpiredException("The account for user [" + username + "] has expired.");
        }

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
            user,
            null,
            user.getAuthorities()
        );
        authentication.setDetails(
            new WebAuthenticationDetailsSource().buildDetails(request)
        );
        securityContextService.setAuthentication(authentication);
    }

    @Override
    public void logUserOut(HttpServletRequest request, HttpServletResponse response)
    {
        request.getSession().invalidate();

        Cookie expiredSessionCookie = cookieService.makeExpiredCookie(Constants.SESSION_COOKIE_NAME);
        Cookie expiredTokenCookie = cookieService.makeExpiredCookie(Constants.JWT_COOKIE_NAME);

        response.addCookie(expiredSessionCookie);
        response.addCookie(expiredTokenCookie);
    }
}