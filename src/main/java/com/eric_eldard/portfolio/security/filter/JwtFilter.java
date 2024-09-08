package com.eric_eldard.portfolio.security.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import org.springframework.security.authentication.AccountStatusException;
import org.springframework.web.filter.OncePerRequestFilter;

import com.eric_eldard.portfolio.service.auth.AuthenticationService;
import com.eric_eldard.portfolio.util.Constants;

/**
 * Validates the user's JWT on each request and puts a fresh user record into the security context. If the user's
 * account status has changed since they logged in (for example, an admin disabled them), it will be caught here.
 * <br><br>
 * All requests pass through this filter, including unauthenticated and even those for public assets.
 */
public class JwtFilter extends OncePerRequestFilter
{
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtFilter.class);

    private final AuthenticationService authenticationService;


    public JwtFilter(AuthenticationService authenticationService)
    {
        this.authenticationService = authenticationService;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws IOException, ServletException
    {
        Optional<Cookie> cookie = getJwtCookie(request);
        if (cookie.isEmpty())
        {
            // Allow request to continue w/o principal; if authz is required, the request will fail once that's reached
            chain.doFilter(request, response);
            return;
        }

        // From this point down, the caller must pass an authentication check. Even if they're
        // requesting a public resource, their token must be well-formed and valid.

        // Get claims and validate
        Jws<Claims> signedToken;
        try
        {
            signedToken = authenticationService.resolveClaims(cookie.get().getValue());

            if (!authenticationService.valid(signedToken))
            {
                handleUnauthorized(request, response, "User presented an invalid auth token; performing logout");
                return; // Don't continue filter chain; kill this request now
            }
        }
        catch (Exception ex)
        {
            handleUnauthorized(request, response, "User presented a malformed auth token; performing logout");
            return; // Don't continue filter chain; kill this request now
        }

        // Currently looking user up on each request.
        // Advantage:
        // - Token info is never stale; no need to track whose permissions have changed since their last login
        // Disadvantage:
        // - 1 extra DB query per request; negates potential JWT benefit
        try
        {
            long userId = Long.parseLong(signedToken.getPayload().getSubject());
            authenticationService.setAuthentication(userId, request);
        }
        catch (Exception ex)
        {
            String message = "Re-authentication failed for reason: " + ex.getMessage();
            int statusCode = ex instanceof AccountStatusException ?
                HttpServletResponse.SC_FORBIDDEN :
                HttpServletResponse.SC_UNAUTHORIZED;
            handleUnauthorizedForStatus(request, response, statusCode, message);
            return; // Don't continue filter chain; kill this request now
        }

        chain.doFilter(request, response);
    }

    private Optional<Cookie> getJwtCookie(HttpServletRequest request)
    {
        Cookie[] cookies = request.getCookies();
        if (cookies == null)
        {
            return Optional.empty();
        }

        return Arrays.stream(cookies)
            .filter(cookie -> cookie.getName().equals(Constants.JWT_COOKIE_NAME))
            .filter(cookie -> StringUtils.isNotBlank(cookie.getValue()))
            .findFirst();
    }

    private void handleUnauthorized(HttpServletRequest request,
                                           HttpServletResponse response,
                                           String message
    ) throws ServletException
    {
        handleUnauthorizedForStatus(request, response, HttpServletResponse.SC_UNAUTHORIZED, message);
    }

    private void handleUnauthorizedForStatus(HttpServletRequest request,
                                                    HttpServletResponse response,
                                                    int statusCode,
                                                    String message
    ) throws ServletException
    {
        LOGGER.info(message);
        request.logout();
        authenticationService.logUserOut(response); // TODO ERIC why is request.logout() not clearing cookies?
        response.setStatus(statusCode);
    }
}