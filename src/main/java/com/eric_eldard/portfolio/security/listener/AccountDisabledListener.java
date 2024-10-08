package com.eric_eldard.portfolio.security.listener;

import lombok.AllArgsConstructor;

import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureDisabledEvent;
import org.springframework.stereotype.Component;

import com.eric_eldard.portfolio.model.user.enumeration.LoginFailureReason;
import com.eric_eldard.portfolio.service.user.PortfolioUserService;

@Component
@AllArgsConstructor
public class AccountDisabledListener implements ApplicationListener<AuthenticationFailureDisabledEvent>
{
    private final PortfolioUserService portfolioUserService;

    @Override
    public void onApplicationEvent(AuthenticationFailureDisabledEvent event)
    {
        String username = event.getAuthentication().getName();
        portfolioUserService.recordFailedLogin(username, LoginFailureReason.ACCOUNT_DISABLED);
    }
}