package com.sun.enhance.io;

import java.io.IOException;

/**
 * Created by zksun on 5/16/16.
 */
public interface ResourcePatternResolver extends ResourceLoader {
    String CLASSPATH_ALL_URL_PREFIX = "classpath*:";

    Resource[] getResources(String locationPattern) throws IOException;
}
