package com.eric_eldard.portfolio.service.classpath;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.URI;
import java.util.List;

@Service
public final class ClasspathServiceImpl implements ClasspathService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ClasspathServiceImpl.class);

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
    public URI getUriOfResource(Resource resource)
    {
        URI fileUri = null;
        try
        {
            fileUri = resource.getURI();
        }
        catch (IOException ex)
        {
            LOGGER.error("Could not load resource [{}]: {}", resource.getFilename(), ex.getMessage());
        }
        return fileUri;
    }
}