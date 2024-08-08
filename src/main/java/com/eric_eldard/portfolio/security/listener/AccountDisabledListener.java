package com.eric_eldard.portfolio.security.listener;

import com.eric_eldard.portfolio.model.user.LoginFailureReason;
import com.eric_eldard.portfolio.service.user.PortfolioUserService;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureDisabledEvent;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class AccountDisabledListener implements ApplicationListener<AuthenticationFailureDisabledEvent>
{
    private final PortfolioUserService portfolioUserService;

    @Inject
    public AccountDisabledListener(PortfolioUserService portfolioUserService)
    {
        this.portfolioUserService = portfolioUserService;
    }

    @Override
    public void onApplicationEvent(AuthenticationFailureDisabledEvent event)
    {
        String username = event.getAuthentication().getName();
        portfolioUserService.recordFailedLogin(username, LoginFailureReason.ACCOUNT_DISABLED);
    }
}