package com.eric_eldard.portfolio.service.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;

@Service
public final class ResourceServiceImpl implements ResourceService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceServiceImpl.class);

    @Override
    public List<Resource> getFileResources(String pathPattern) throws IOException
    {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("file:" + pathPattern);
        return List.of(resources);
    }

    @Override
    public List<Resource> getClasspathResources(String classpathPattern) throws IOException
    {
        ClassLoader loader = this.getClass().getClassLoader();
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(loader);
        Resource[] resources = resolver.getResources(classpathPattern) ;
        return List.of(resources);
    }

    @Override
    @Nullable
    public String getPathOfResource(Resource resource)
    {
        String fileUri = null;
        try
        {
            fileUri = resource.getURI().getPath();
        }
        catch (IOException ex)
        {
            LOGGER.error("Could not load resource [{}]: {}", resource.getFilename(), ex.getMessage());
        }
        return fileUri;
    }
}