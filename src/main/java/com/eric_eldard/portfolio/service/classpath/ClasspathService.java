package com.eric_eldard.portfolio.service.classpath;

import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.List;

/**
 * Utilities for working with classpath resources
 */
public interface ClasspathService
{
    /**
     * Get a list of resources from the classpath 1 layer deep (non-recursive).
     */
    List<Resource> getClasspathResources(String classpath) throws IOException;
}