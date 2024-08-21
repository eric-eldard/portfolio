package com.eric_eldard.portfolio.security.listener;

import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureExpiredEvent;
import org.springframework.stereotype.Component;

import com.eric_eldard.portfolio.model.user.enumeration.LoginFailureReason;
import com.eric_eldard.portfolio.service.user.PortfolioUserService;

@Component
public class AccountExpiredListener implements ApplicationListener<AuthenticationFailureExpiredEvent>
{
    private final PortfolioUserService portfolioUserService;

    public AccountExpiredListener(PortfolioUserService portfolioUserService)
    {
        this.portfolioUserService = portfolioUserService;
    }

    @Override
    public void onApplicationEvent(AuthenticationFailureExpiredEvent event)
    {
        String username = event.getAuthentication().getName();
        portfolioUserService.recordFailedLogin(username, LoginFailureReason.ACCOUNT_EXPIRED);
    }
}