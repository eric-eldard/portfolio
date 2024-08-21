package com.eric_eldard.portfolio.security.listener;

import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureLockedEvent;
import org.springframework.stereotype.Component;

import com.eric_eldard.portfolio.model.user.enumeration.LoginFailureReason;
import com.eric_eldard.portfolio.service.user.PortfolioUserService;

@Component
public class AccountLockedListener implements ApplicationListener<AuthenticationFailureLockedEvent>
{
    private final PortfolioUserService portfolioUserService;

    public AccountLockedListener(PortfolioUserService portfolioUserService)
    {
        this.portfolioUserService = portfolioUserService;
    }

    @Override
    public void onApplicationEvent(AuthenticationFailureLockedEvent event)
    {
        String username = event.getAuthentication().getName();
        portfolioUserService.recordFailedLogin(username, LoginFailureReason.ACCOUNT_LOCKED);;
    }
}