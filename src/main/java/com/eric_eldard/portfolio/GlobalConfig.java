package com.eric_eldard.portfolio;

import com.eric_eldard.portfolio.persistence.user.PortfolioUserRepository;
import com.eric_eldard.portfolio.service.user.PortfolioUserService;
import com.eric_eldard.portfolio.service.user.PortfolioUserServiceImpl;
import com.eric_eldard.portfolio.service.user.SecurityContextService;
import org.slf4j.MDC;
import org.springframework.boot.web.servlet.server.CookieSameSiteSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.security.web.header.writers.CrossOriginResourcePolicyHeaderWriter;

import javax.inject.Inject;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class GlobalConfig
{
    private final PasswordEncoder passwordEncoder;

    private final PortfolioUserServiceImpl portfolioUserDetailsService;

    private final SecurityContextService securityContextService;

    @Inject
    public GlobalConfig(SecurityContextService securityContextService, PortfolioUserRepository portfolioUserRepo)
    {
        this.securityContextService = securityContextService;
        passwordEncoder = new BCryptPasswordEncoder();
        portfolioUserDetailsService =
            new PortfolioUserServiceImpl(passwordEncoder, portfolioUserRepo, securityContextService);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
        throws Exception
    {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public CookieSameSiteSupplier applicationCookieSameSiteSupplier()
    {
        return CookieSameSiteSupplier.ofStrict();
    }

    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return passwordEncoder;
    }

    @Bean
    public PortfolioUserService portfolioUserService()
    {
        return portfolioUserDetailsService;
    }

    @Bean
    public SecurityFilterChain portfolioFilterChain(HttpSecurity httpSecurity) throws Exception
    {
        httpSecurity
            .authorizeHttpRequests((config) -> config
                .antMatchers("/").permitAll()
                .antMatchers("/portfolio/**").authenticated()
                .antMatchers("/portfolio/users/**").hasRole("ADMIN")
            )
            .authenticationManager(
                makeAuthenticationManager(httpSecurity)
            )
            .httpBasic()
                .and()
            .headers()
                .frameOptions()
                    .sameOrigin()
                .crossOriginResourcePolicy()
                    .policy(CrossOriginResourcePolicyHeaderWriter.CrossOriginResourcePolicy.SAME_SITE)
                    .and()
                .and()
            .securityContext((securityContext) -> securityContext.requireExplicitSave(true))
            .addFilterAfter(this::addUserToMdc, SecurityContextHolderFilter.class);

        return httpSecurity.build();
    }

    private AuthenticationManager makeAuthenticationManager(HttpSecurity httpSecurity) throws Exception
    {
        AuthenticationManagerBuilder builder = httpSecurity.getSharedObject(AuthenticationManagerBuilder.class);
        builder.userDetailsService(portfolioUserDetailsService).passwordEncoder(passwordEncoder);
        return builder.build();
    }

    private void addUserToMdc(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
        throws IOException, ServletException
    {
        try
        {
            String username = securityContextService.getCurrentUsersNameNullable();
            if (username != null)
            {
                MDC.put("username", username);
            }
            filterChain.doFilter(servletRequest, servletResponse);
        }
        finally
        {
            MDC.remove("username");
        }
    }
}