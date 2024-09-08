package com.eric_eldard.portfolio.security.listener;

import lombok.AllArgsConstructor;

import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import com.eric_eldard.portfolio.service.user.PortfolioUserService;

@Component
@AllArgsConstructor
public class LoginSuccessListener implements ApplicationListener<AuthenticationSuccessEvent>
{
    private final PortfolioUserService portfolioUserService;

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event)
    {
        String username = event.getAuthentication().getName();
        portfolioUserService.recordSuccessfulLogin(username);
    }
}