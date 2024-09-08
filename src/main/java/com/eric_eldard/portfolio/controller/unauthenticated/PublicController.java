package com.eric_eldard.portfolio.controller.unauthenticated;

import java.security.Principal;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Unauthenticated endpoints
 */
@Controller
public class PublicController
{
    @GetMapping("/")
    public String home()
    {
        return "home";
    }

    @GetMapping("/login")
    public String login(Principal principal)
    {
        if (principal != null && ((Authentication) principal).isAuthenticated())
        {
            return "redirect:/portfolio";
        }
        return "login";
    }
}