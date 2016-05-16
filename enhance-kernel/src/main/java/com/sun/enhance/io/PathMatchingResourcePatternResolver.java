package com.sun.enhance.io;

import com.sun.enhance.logging.Logger;
import com.sun.enhance.logging.LoggerFactory;
import com.sun.enhance.scan.AntPathMatcher;
import com.sun.enhance.scan.PathMatcher;
import com.sun.enhance.util.Assert;

import java.io.IOException;

/**
 * Created by zksun on 5/16/16.
 */
public class PathMatchingResourcePatternResolver implements ResourcePatternResolver {

    private static final Logger logger = LoggerFactory.getLogger(PathMatchingResourcePatternResolver.class);

    private final ResourceLoader resourceLoader;

    private PathMatcher pathMatcher = new AntPathMatcher();

    public PathMatchingResourcePatternResolver() {
        this.resourceLoader = new DefaultResourceLoader();
    }

    public PathMatchingResourcePatternResolver(java.lang.ClassLoader classLoader) {
        this.resourceLoader = new DefaultResourceLoader(classLoader);
    }

    public PathMatchingResourcePatternResolver(ResourceLoader resourceLoader) {
        Assert.notNull(resourceLoader, "ResourceLoader must not be null");
        this.resourceLoader = resourceLoader;
    }

    public ResourceLoader getResourceLoader() {
        return this.resourceLoader;
    }

    public void setPathMatcher(PathMatcher pathMatcher) {
        Assert.notNull(pathMatcher, "PathMatcher must not be null");
        this.pathMatcher = pathMatcher;
    }


    @Override
    public Resource[] getResources(String locationPattern) throws IOException {
        return new Resource[0];
    }

    @Override
    public ClassLoader getClassLoader() {
        return getResourceLoader().getClassLoader();
    }
}
