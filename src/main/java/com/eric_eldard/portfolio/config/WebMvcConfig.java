package com.eric_eldard.portfolio.config;

import com.eric_eldard.portfolio.interceptor.VersionInterceptor;
import com.eric_eldard.portfolio.util.Constants;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.TimeUnit;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer
{
    private final VersionInterceptor versionInterceptor;

    public WebMvcConfig(VersionInterceptor versionInterceptor)
    {
        this.versionInterceptor = versionInterceptor;
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer)
    {
        configurer.setUseTrailingSlashMatch(true);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry)
    {
        registry.addInterceptor(versionInterceptor);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry)
    {
        // Exposes protected assets for any authenticated user
        registry.addResourceHandler("/portfolio/assets/**")
            .addResourceLocations("classpath:" + Constants.ASSETS_PATH + "/")
            .setCacheControl(CacheControl.maxAge(1, TimeUnit.DAYS));

        // Public assets already exposed; this adds cache control to public images
        registry.addResourceHandler("/public/assets/images/**")
            .addResourceLocations("/public/assets/images/")
            .setCacheControl(CacheControl.maxAge(1, TimeUnit.DAYS));

        // Exposes old portfolio versions for admins
        registry.addResourceHandler("/portfolio/old/**")
            .addResourceLocations("classpath:" + Constants.OLD_PORTFOLIO_PATH + "/")
            .setCacheControl(CacheControl.maxAge(1, TimeUnit.DAYS));
    }

    /**
     * Adds redirects for index.html pages of prior portfolio versions
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry)
    {
        registry.addRedirectViewController(Constants.OLD_PORTFOLIO_PATH, "/portfolio/old/index.html");
        registry.addRedirectViewController(Constants.OLD_PORTFOLIO_PATH + "/old", "/portfolio/old/old/index.html");
    }
}