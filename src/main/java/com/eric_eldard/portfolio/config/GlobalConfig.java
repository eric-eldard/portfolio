package com.eric_eldard.portfolio.config;

import static org.springframework.security.web.header.writers.CrossOriginResourcePolicyHeaderWriter.CrossOriginResourcePolicy.SAME_SITE;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.server.CookieSameSiteSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
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

import jakarta.servlet.DispatcherType;
import java.util.Collection;

import com.eric_eldard.portfolio.logging.AddUserToMdcFilter;
import com.eric_eldard.portfolio.model.AdditionalLocation;
import com.eric_eldard.portfolio.persistence.user.PortfolioUserRepository;
import com.eric_eldard.portfolio.properties.AdditionalLocations;
import com.eric_eldard.portfolio.service.user.PortfolioUserService;
import com.eric_eldard.portfolio.service.user.PortfolioUserServiceImpl;
import com.eric_eldard.portfolio.service.user.SecurityContextService;

/**
 * Master config for security, logging, and beans for which creation order prevents a circular dependency.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@EnableConfigurationProperties(AdditionalLocations.class)
@PropertySource(value = "file:${portfolio.additional-properties.location}", ignoreResourceNotFound = true)
public class GlobalConfig
{
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalConfig.class);

    private final AdditionalLocations additionalLocations;

    private final PasswordEncoder passwordEncoder;

    private final PortfolioUserServiceImpl portfolioUserDetailsService;

    private final SecurityContextService securityContextService;

    public GlobalConfig(AdditionalLocations additionalLocations,
                        PortfolioUserRepository portfolioUserRepo,
                        SecurityContextService securityContextService
    )
    {
        this.additionalLocations = additionalLocations;
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
        authorizeAdditionalLocationRequests(httpSecurity, additionalLocations.getLocations());

        httpSecurity
            .authorizeHttpRequests(requests -> requests
                // WARNING: order matters, since these paths are hierarchical; putting "/" 1st gives admin access to all
                .requestMatchers("/portfolio/users/**").hasRole("ADMIN")
                .requestMatchers("/portfolio/**").authenticated()
                .requestMatchers("/", "/public/assets/**").permitAll()
                .dispatcherTypeMatchers(
                    DispatcherType.ERROR,
                    DispatcherType.FORWARD,
                    DispatcherType.INCLUDE
                ).permitAll()
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

    /**
     * Adds HTTP authorization rules to requests for {@link AdditionalLocation}s
     */
    private void authorizeAdditionalLocationRequests(HttpSecurity httpSecurity,
                                                     Collection<AdditionalLocation> locations)
    {
        locations.forEach(location ->
        {
            String webPath = location.webPath();
            String authority = location.authority().toString();
            try
            {
                httpSecurity.authorizeHttpRequests(requests ->
                    requests.requestMatchers(webPath + "/**").hasAuthority(authority)
                );
            }
            catch (Exception ex)
            {
                LOGGER.error("Unable to load additional location [{}] for reason [{}]", webPath, ex.getMessage());
            }
        });
    }

    private AuthenticationManager makeAuthenticationManager(HttpSecurity httpSecurity) throws Exception
    {
        AuthenticationManagerBuilder builder = httpSecurity.getSharedObject(AuthenticationManagerBuilder.class);
        builder.userDetailsService(portfolioUserDetailsService).passwordEncoder(passwordEncoder);
        return builder.build();
    }
}