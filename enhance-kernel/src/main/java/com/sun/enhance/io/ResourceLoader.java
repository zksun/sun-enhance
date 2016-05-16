package com.sun.enhance.io;


import com.sun.enhance.util.ResourceUtils;

/**
 * Created by zksun on 5/16/16.
 */
public interface ResourceLoader {
    String CLASSPATH_URL_PREFIX = ResourceUtils.CLASSPATH_URL_PREFIX;

    ClassLoader getClassLoader();

}
