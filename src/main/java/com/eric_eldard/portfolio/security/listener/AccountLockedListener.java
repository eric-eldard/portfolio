package com.eric_eldard.portfolio.security.listener;

import lombok.AllArgsConstructor;

import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureLockedEvent;
import org.springframework.stereotype.Component;

import com.eric_eldard.portfolio.model.user.enumeration.LoginFailureReason;
import com.eric_eldard.portfolio.service.user.PortfolioUserService;

@Component
@AllArgsConstructor
public class AccountLockedListener implements ApplicationListener<AuthenticationFailureLockedEvent>
{
    private final PortfolioUserService portfolioUserService;

    @Override
    public void onApplicationEvent(AuthenticationFailureLockedEvent event)
    {
        String username = event.getAuthentication().getName();
        portfolioUserService.recordFailedLogin(username, LoginFailureReason.ACCOUNT_LOCKED);;
    }
}