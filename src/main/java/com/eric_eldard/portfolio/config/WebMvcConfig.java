package com.eric_eldard.portfolio.config;

import lombok.AllArgsConstructor;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.eric_eldard.portfolio.interceptor.VersionInterceptor;
import com.eric_eldard.portfolio.model.AdditionalLocation;
import com.eric_eldard.portfolio.properties.AdditionalLocations;
import com.eric_eldard.portfolio.service.resource.ResourceService;
import com.eric_eldard.portfolio.util.Constants;
import com.eric_eldard.portfolio.util.StringUtils;

@Configuration
@AllArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer
{
    private final AdditionalLocations additionalLocations;

    private final ResourceService resourceService;

    private final VersionInterceptor versionInterceptor;

    @Value("${portfolio.assets-path}")
    private final String assetsFilePath;

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
        // Maps favicon
        registry.addResourceHandler("/favicon.ico")
            .addResourceLocations("/public/assets/images/icons/favicon/favicon.ico")
            .setCacheControl(CacheControl.maxAge(1, TimeUnit.DAYS));

        // Public classpath assets already exposed; this adds cache control to public images
        registry.addResourceHandler("/public/assets/images/**")
            .addResourceLocations("/public/assets/images/")
            .setCacheControl(CacheControl.maxAge(1, TimeUnit.DAYS));

        // Exposes protected assets, stored in the file system, to authenticated users
        String assetsFilePath = StringUtils.withTrailingString(this.assetsFilePath, "/");
        registry.addResourceHandler(Constants.ASSETS_PATH + "/**")
            .addResourceLocations(resourceService.resolveResource(assetsFilePath))
            .setCacheControl(CacheControl.maxAge(1, TimeUnit.DAYS));

        // Exposes additional locations to which only those with the matching granted authority will have access
        for (AdditionalLocation location : additionalLocations.getLocations())
        {
            String basePath = StringUtils.withTrailingString(location.basePath(), "/");
            Resource resource = resourceService.resolveResource(basePath);
            registry.addResourceHandler(location.webPath() + "/**")
                .addResourceLocations(resource);
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
            String webPath = StringUtils.withoutTrailingString(location.webPath(), "/");
            registry.addRedirectViewController(webPath,       webPath + "/index.html");
            registry.addRedirectViewController(webPath + '/', webPath + "/index.html");
        }
    }
}