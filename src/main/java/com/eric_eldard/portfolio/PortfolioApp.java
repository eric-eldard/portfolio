package com.eric_eldard.portfolio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(
    scanBasePackages = "com.eric_eldard.portfolio",
    exclude = SecurityAutoConfiguration.class
)
public class PortfolioApp
{
    public static void main(String[] args)
    {
        SpringApplication.run(PortfolioApp.class, args);
    }
}