package com.eric_eldard.portfolio.controller.unauthenticated;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.eric_eldard.portfolio.model.auth.Credentials;
import com.eric_eldard.portfolio.model.user.PortfolioUser;
import com.eric_eldard.portfolio.service.auth.AuthenticationService;

@Slf4j
@RestController
@AllArgsConstructor
public class AuthenticationController
{
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public void login(@RequestBody @Valid Credentials credentials,
                      HttpServletRequest request,
                      HttpServletResponse response
    )
    {
        try
        {
            Authentication authentication = authenticationService.authenticate(
                credentials.getUsername(),
                credentials.getPassword()
            );

            log.info("[{}] successfully logged in.", authentication.getName());

            PortfolioUser user = (PortfolioUser) authentication.getPrincipal();
            String token = authenticationService.issueToken(user);

            authenticationService.setAuthenticationForRequest(token, request, response);

            response.sendRedirect("/portfolio");
        }
        catch (Exception ex)
        {
            log.info("Authentication failed for reason: {}", ex.getMessage());
            response.setStatus(ex instanceof AccountStatusException ?
                HttpServletResponse.SC_FORBIDDEN :
                HttpServletResponse.SC_UNAUTHORIZED
            );
        }
    }
}