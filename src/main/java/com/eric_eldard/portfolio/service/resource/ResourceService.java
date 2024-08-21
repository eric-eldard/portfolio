package com.eric_eldard.portfolio.service.resource;

import org.springframework.core.io.Resource;

import jakarta.annotation.Nullable;
import java.io.IOException;
import java.util.List;

/**
 * Utilities for working with file and classpath resources
 */
public interface ResourceService
{
    /**
     * Get a list of resources from the file system using the provided pattern. Supports wildcards * and **.
     */
    List<Resource> getFileResources(String pathPattern) throws IOException;

    /**
     * Get a list of resources from the classpath using the provided pattern. Supports wildcards * and **.
     */
    List<Resource> getClasspathResources(String classpathPattern) throws IOException;

    /**
     * Swallow and log checked exceptions coming out of {@link Resource#getURI()}
     */
    @Nullable
    String getPathOfResource(Resource resource);
}