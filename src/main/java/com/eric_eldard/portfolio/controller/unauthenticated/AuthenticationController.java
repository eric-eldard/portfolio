package com.eric_eldard.portfolio.controller.unauthenticated;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;

import com.eric_eldard.portfolio.model.auth.Credentials;
import com.eric_eldard.portfolio.model.user.PortfolioUser;
import com.eric_eldard.portfolio.service.auth.AuthenticationService;
import com.eric_eldard.portfolio.service.web.CookieService;
import com.eric_eldard.portfolio.util.Constants;

@RestController
public class AuthenticationController
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationController.class);

    private final AuthenticationService authenticationService;

    private final CookieService cookieService;


    public AuthenticationController(AuthenticationService authenticationService, CookieService cookieService)
    {
        this.authenticationService = authenticationService;
        this.cookieService = cookieService;
    }


    @PostMapping
    @RequestMapping("/login")
    public void login(@RequestBody @Valid Credentials credentials,
                      HttpServletRequest request,
                      HttpServletResponse response
    ) throws IOException
    {
        try
        {
            Authentication authentication = authenticationService.authenticate(
                credentials.getUsername(),
                credentials.getPassword()
            );

            PortfolioUser user = (PortfolioUser) authentication.getPrincipal();
            String authToken = authenticationService.issueToken(user);

            authenticationService.setAuthentication(user, request);

            Cookie tokenCookie = cookieService.makeCookie(Constants.JWT_COOKIE_NAME, authToken);

            response.addCookie(tokenCookie);
            response.sendRedirect("/portfolio");
        }
        catch (AuthenticationException ex)
        {
            LOGGER.info("{} for user [{}]", ex.getMessage(), credentials.getUsername());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    @GetMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        authenticationService.logUserOut(request, response);
        response.sendRedirect("/");
    }
}