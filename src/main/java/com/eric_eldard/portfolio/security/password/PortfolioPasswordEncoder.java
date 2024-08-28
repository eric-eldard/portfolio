package com.eric_eldard.portfolio.security.password;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PortfolioPasswordEncoder extends BCryptPasswordEncoder
{
}