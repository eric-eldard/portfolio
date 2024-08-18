package com.eric_eldard.portfolio.service.classpath;

import org.springframework.core.io.Resource;

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.URI;
import java.util.List;

/**
 * Utilities for working with classpath resources
 */
public interface ClasspathService
{
    /**
     * Get a list of resources from the classpath using the provided pattern. Supports wildcards * and **.
     */
    List<Resource> getClasspathResources(String classpathPattern) throws IOException;

    /**
     * Swallow and log checked exceptions coming out of {@link Resource#getURI()}
     */
    @Nullable
    URI getUriOfResource(Resource resource);
}