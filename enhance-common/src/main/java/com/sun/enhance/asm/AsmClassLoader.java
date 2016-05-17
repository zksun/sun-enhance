package com.sun.enhance.asm;

import java.security.PrivilegedAction;

/**
 * Created by zksun on 5/17/16.
 */
public class AsmClassLoader extends ClassLoader {
    private static java.security.ProtectionDomain DOMAIN;

    static {
        DOMAIN = (java.security.ProtectionDomain) java.security.AccessController.doPrivileged(new PrivilegedAction<Object>() {
            public Object run() {
                return AsmClassLoader.class.getProtectionDomain();
            }
        });
    }

    public AsmClassLoader() {
        super(AsmClassLoader.class.getClassLoader());
    }

    public Class<?> defineClassPublic(String name, byte[] b, int off, int len) throws ClassFormatError {
        Class<?> clazz = defineClass(name, b, off, len, DOMAIN);
        return clazz;
    }


}
