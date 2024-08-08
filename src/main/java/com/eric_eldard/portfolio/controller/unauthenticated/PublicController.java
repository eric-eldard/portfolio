package com.eric_eldard.portfolio.controller.unauthenticated;

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
}