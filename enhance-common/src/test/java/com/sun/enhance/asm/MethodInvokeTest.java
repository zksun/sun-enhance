package com.sun.enhance.asm;

import com.sun.enhance.domain.SayHello;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.Method;

/**
 * Created by zksun on 2019/7/2.
 */
public class MethodInvokeTest {
    @Test
    public void testMethodName() {
        Method[] methods = SayHello.class.getDeclaredMethods();
        for (Method method : methods) {
            System.out.println(method.getName());
        }
    }

    @Test
    public void testDescriptionClazz() {
        String canonicalName = SayHello.class.getCanonicalName();
        String simpleName = SayHello.class.getSimpleName();
        System.out.println("canonicalName is: " + canonicalName);
        System.out.println("simpleName is: " + simpleName);
        System.out.println("sys canonicalName: " + canonicalName.replace('.', File.separatorChar));
    }


}
