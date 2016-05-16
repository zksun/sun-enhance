package com.sun.enhance.io;

import com.sun.enhance.logging.Logger;
import com.sun.enhance.logging.LoggerFactory;
import com.sun.org.apache.bcel.internal.util.ClassLoader;

import java.io.IOException;

/**
 * Created by zksun on 5/16/16.
 */
public class PathMatchingResourcePatternResolver implements ResourcePatternResolver {

    private static final Logger logger = LoggerFactory.getLogger(PathMatchingResourcePatternResolver.class);

   // private final ResourceLoader resourceLoader;

    @Override
    public Resource[] getResources(String locationPattern) throws IOException {
        return new Resource[0];
    }

    @Override
    public ClassLoader getClassLoader() {
        return null;
    }
}
