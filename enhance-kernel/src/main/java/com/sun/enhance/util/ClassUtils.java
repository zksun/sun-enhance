package com.sun.enhance.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * Created by zksun on 5/12/16.
 */
public final class ClassUtils {

    private static Method CLASS_DEFINITION_METHOD;

    static {
        try {
            CLASS_DEFINITION_METHOD = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);
            CLASS_DEFINITION_METHOD.setAccessible(true);
        } catch (NoSuchMethodException e) {
        }
    }

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

    public static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Throwable ex) {
            // Cannot access thread context ClassLoader - falling back to system class loader...
        }
        if (cl == null) {
            // No thread context class loader -> use class loader of this class.
            cl = ClassUtils.class.getClassLoader();
        }
        return cl;
    }

    public static String classPackageAsResourcePath(Class<?> clazz) {
        if (clazz == null) {
            return "";
        }
        String className = clazz.getName();
        int packageEndIndex = className.lastIndexOf('.');
        if (packageEndIndex == -1) {
            return "";
        }
        String packageName = className.substring(0, packageEndIndex);
        return packageName.replace('.', '/');
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

    public static String getClassPath() {
        return System.getProperty("java.class.path");
    }

    public static String getUserHomePath() {
        return System.getProperty("user.home");
    }

    public static String getUserDirPath() {
        return System.getProperty("user.dir");
    }


    public static void classRedefine(String className, byte[] code) throws Exception {
        if (StringUtils.isEmpty(className)) {
            throw new NullPointerException("className");
        }
        if (null == code || code.length < 1) {
            throw new NullPointerException("code");
        }
        CLASS_DEFINITION_METHOD.invoke(ClassLoader.getSystemClassLoader(), className, code, 0, code.length);
    }
}
