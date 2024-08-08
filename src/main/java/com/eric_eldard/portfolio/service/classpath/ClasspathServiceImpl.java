package com.eric_eldard.portfolio.service.classpath;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public final class ClasspathServiceImpl implements ClasspathService
{
    @Override
    public List<Resource> getClasspathResources(String classpath) throws IOException
    {
        ClassLoader loader = this.getClass().getClassLoader();
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(loader);
        Resource[] resources = resolver.getResources(classpath) ;
        return List.of(resources);
    }
}