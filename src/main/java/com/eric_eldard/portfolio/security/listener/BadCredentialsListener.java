package com.eric_eldard.portfolio.security.listener;

import lombok.AllArgsConstructor;

import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

import com.eric_eldard.portfolio.model.user.enumeration.LoginFailureReason;
import com.eric_eldard.portfolio.service.user.PortfolioUserService;

@Component
@AllArgsConstructor
public class BadCredentialsListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent>
{
    private final PortfolioUserService portfolioUserService;

    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event)
    {
        String username = (String) event.getAuthentication().getPrincipal();
        portfolioUserService.recordFailedLogin(username, LoginFailureReason.BAD_CREDENTIALS);
    }
}