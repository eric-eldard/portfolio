package com.eric_eldard.portfolio.service.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Date;

import com.eric_eldard.portfolio.model.user.PortfolioUser;

public interface AuthenticationService
{
    Authentication authenticate(String username, String password);

    String issueToken(PortfolioUser user);

    String issueToken(PortfolioUser user, Date issuedAt, Date expiry);

    Jws<Claims> resolveClaims(String token);

    boolean valid(Jws<Claims> signedToken) throws AuthenticationException;

    void setAuthentication(long userId, HttpServletRequest source);

    void setAuthentication(PortfolioUser user, HttpServletRequest source);

    void logUserOut(HttpServletRequest request, HttpServletResponse response);
}