package com.sun.enhance.asm;

import java.io.Serializable;

/**
 * Created by zksun on 5/11/16.
 */
public final class Clazz implements Serializable {

    private String name;
    private String classCanonicalName;
    private Feeld[] fields;
    private Mezhod[] methods;
    private Class<?>[] interfaces;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Feeld[] getFields() {
        return fields;
    }

    public void setFields(Feeld[] fields) {
        this.fields = fields;
    }

    public Mezhod[] getMethods() {
        return methods;
    }

    public void setMethods(Mezhod[] methods) {
        this.methods = methods;
    }

    public Class<?>[] getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(Class<?>[] interfaces) {
        this.interfaces = interfaces;
    }

    public String getClassCanonicalName() {
        return classCanonicalName;
    }

    public void setClassCanonicalName(String classCanonicalName) {
        this.classCanonicalName = classCanonicalName;
    }
}
