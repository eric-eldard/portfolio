package com.eric_eldard.portfolio.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.TimeUnit;

import com.eric_eldard.portfolio.interceptor.VersionInterceptor;
import com.eric_eldard.portfolio.model.AdditionalLocation;
import com.eric_eldard.portfolio.properties.AdditionalLocations;
import com.eric_eldard.portfolio.util.Constants;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer
{
    private final AdditionalLocations additionalLocations;

    private final VersionInterceptor versionInterceptor;

    private final String assetsFilePath;

    public WebMvcConfig(
        AdditionalLocations additionalLocations,
        VersionInterceptor versionInterceptor,
        @Value("${portfolio.assets-path}") String assetsFilePath
    )
    {
        this.versionInterceptor = versionInterceptor;
        this.assetsFilePath = assetsFilePath;
        this.additionalLocations = additionalLocations;
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
        // Public classpath assets already exposed; this adds cache control to public images
        registry.addResourceHandler("/public/assets/images/**")
            .addResourceLocations("/public/assets/images/")
            .setCacheControl(CacheControl.maxAge(1, TimeUnit.DAYS));

        // Exposes protected assets, stored in the file system, to authenticated users
        registry.addResourceHandler(Constants.ASSETS_PATH + "/**")
            .addResourceLocations(new FileSystemResource(assetsFilePath + '/'))
            .setCacheControl(CacheControl.maxAge(1, TimeUnit.DAYS));

        // Exposes additional locations to which only admins will have access
        for (AdditionalLocation location : additionalLocations.getLocations())
        {
            registry.addResourceHandler(location.webPath() + "/**")
                .addResourceLocations(new FileSystemResource(location.filePath() + '/'));
        }
    }

    /**
     * Adds redirects to index.html pages for {@link AdditionalLocation}s
     * @see AdditionalLocations
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry)
    {
        for (AdditionalLocation location : additionalLocations.getLocations())
        {
            String webPath = location.webPath();
            registry.addRedirectViewController(webPath, webPath + "/index.html");
        }
    }
}