package com.eric_eldard.portfolio.service.auth;

import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;

import com.eric_eldard.portfolio.model.user.PortfolioUser;

public interface AuthenticationService
{
    Authentication authenticate(String username, String password);

    String issueToken(PortfolioUser user);

    void setAuthenticationForRequest(String token, HttpServletRequest request, HttpServletResponse response);

    void logUserOut(HttpServletResponse response, @Nullable Authentication authentication);

    /**
     * When an account is changed in a way that will require a fresh lookup (disabled, permissions changed, etc.), we'll
     * store the user's ID for the TTL of an auth token, to ensure any stale token presented prior to its expiry has its
     * claims refreshed. (All tokens issued prior to a server restart also require a claims refresh, since this list of
     * users is lost.)
     */
    void requireFreshClaimsForUser(long userId);
}