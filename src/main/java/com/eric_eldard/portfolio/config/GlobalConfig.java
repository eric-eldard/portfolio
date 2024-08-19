package com.eric_eldard.portfolio.config;

import com.eric_eldard.portfolio.logging.AddUserToMdcFilter;
import com.eric_eldard.portfolio.persistence.user.PortfolioUserRepository;
import com.eric_eldard.portfolio.service.user.PortfolioUserService;
import com.eric_eldard.portfolio.service.user.PortfolioUserServiceImpl;
import com.eric_eldard.portfolio.service.user.SecurityContextService;
import jakarta.servlet.DispatcherType;
import org.springframework.boot.web.servlet.server.CookieSameSiteSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter;

import static org.springframework.security.web.header.writers.CrossOriginResourcePolicyHeaderWriter.CrossOriginResourcePolicy.SAME_SITE;

/**
 * Master config for security, logging, and beans for which creation order prevents a circular dependency.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class GlobalConfig
{
    private final PasswordEncoder passwordEncoder;

    private final PortfolioUserServiceImpl portfolioUserDetailsService;

    private final SecurityContextService securityContextService;

    public GlobalConfig(SecurityContextService securityContextService, PortfolioUserRepository portfolioUserRepo)
    {
        this.securityContextService = securityContextService;
        passwordEncoder = new BCryptPasswordEncoder();

        // Creating this bean here, instead of annotating its class for component scan, avoids a circular dependency
        // with GlobalConfig and PasswordEncoder,
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
            .authorizeHttpRequests(requests -> requests
                .dispatcherTypeMatchers(
                    DispatcherType.ERROR,
                    DispatcherType.FORWARD,
                    DispatcherType.INCLUDE
                ).permitAll()
                // WARNING: order matters, since these paths are hierarchical; putting "/" 1st gives admin access to all
                .requestMatchers("/portfolio/users/**").hasRole("ADMIN")
                .requestMatchers("/portfolio/**").authenticated()
                .requestMatchers("/", "/public/assets/**").permitAll()
            )
            .authenticationManager(
                makeAuthenticationManager(httpSecurity)
            )
            .httpBasic(
                Customizer.withDefaults()
            )
            .securityContext(security -> security
                .requireExplicitSave(false) // makes sec context available for logging, even on unauthenticated pages
            )
            .headers(headers -> headers
                .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                .crossOriginResourcePolicy(crossOrigin -> crossOrigin.policy(SAME_SITE))
            )
            .addFilterAfter(
                new AddUserToMdcFilter(securityContextService), SecurityContextHolderAwareRequestFilter.class
            );

        return httpSecurity.build();
    }

    private AuthenticationManager makeAuthenticationManager(HttpSecurity httpSecurity) throws Exception
    {
        AuthenticationManagerBuilder builder = httpSecurity.getSharedObject(AuthenticationManagerBuilder.class);
        builder.userDetailsService(portfolioUserDetailsService).passwordEncoder(passwordEncoder);
        return builder.build();
    }
}