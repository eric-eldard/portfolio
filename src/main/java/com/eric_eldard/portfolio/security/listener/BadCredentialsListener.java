package com.eric_eldard.portfolio.security.listener;

import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

import com.eric_eldard.portfolio.model.user.enumeration.LoginFailureReason;
import com.eric_eldard.portfolio.service.user.PortfolioUserService;

@Component
public class BadCredentialsListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent>
{
    private final PortfolioUserService portfolioUserService;

    public BadCredentialsListener(PortfolioUserService portfolioUserService)
    {
        this.portfolioUserService = portfolioUserService;
    }

    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event)
    {
        String username = event.getAuthentication().getName();
        portfolioUserService.recordFailedLogin(username, LoginFailureReason.BAD_CREDENTIALS);
    }
}