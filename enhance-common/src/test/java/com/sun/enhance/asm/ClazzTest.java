package com.sun.enhance.asm;

import com.sun.enhance.doamin.TestClazz;

import java.lang.reflect.Method;

/**
 * Created by zksun on 5/11/16.
 */
public class ClazzTest {
    public static void main(String[] args) {
        try {
            Class<?> aClass = Class.forName(TestClazz.class.getCanonicalName());
            Method[] methods = aClass.getDeclaredMethods();

            System.out.println("end");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}

