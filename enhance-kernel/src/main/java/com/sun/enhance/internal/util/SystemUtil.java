package com.sun.enhance.internal.util;

import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * Created by zksun on 5/12/16.
 */
public final class SystemUtil {
    /**
     * @return
     */
    public static ClassLoader getContextClassLoader() {
        if (System.getSecurityManager() == null) {
            return Thread.currentThread().getContextClassLoader();
        } else {
            return AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
                @Override
                public ClassLoader run() {
                    return Thread.currentThread().getContextClassLoader();
                }
            });
        }
    }

    /**
     * @param clazz
     * @return
     */
    public static ClassLoader getClassLoader(final Class<?> clazz) {
        if (null == System.getSecurityManager()) {
            return clazz.getClassLoader();
        } else {
            return AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
                @Override
                public ClassLoader run() {
                    return clazz.getClassLoader();
                }
            });
        }
    }


    /**
     * @param className
     * @param initialize
     * @param classLoader
     * @return
     * @throws ClassNotFoundException
     */
    public static Class loadClass(String className, boolean initialize, ClassLoader classLoader) throws ClassNotFoundException {
        return Class.forName(className, initialize, classLoader);
    }
}
