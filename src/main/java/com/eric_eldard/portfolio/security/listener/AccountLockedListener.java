package com.eric_eldard.portfolio.security.listener;

import com.eric_eldard.portfolio.model.user.LoginFailureReason;
import com.eric_eldard.portfolio.service.user.PortfolioUserService;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureLockedEvent;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class AccountLockedListener implements ApplicationListener<AuthenticationFailureLockedEvent>
{
    private final PortfolioUserService portfolioUserService;

    @Inject
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