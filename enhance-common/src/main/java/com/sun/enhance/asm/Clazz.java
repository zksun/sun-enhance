package com.sun.enhance.asm;

import java.lang.annotation.Annotation;
import java.util.Map;

import static com.sun.enhance.asm.AsmUtil.*;


/**
 * Created by zksun on 5/11/16.
 */
public final class Clazz extends Member {
    private String classCanonicalName;
    private Feeld[] fields;
    private Mezhod[] methods;
    private Class<?>[] interfaces;
    private Map<Annotation, Annoteition> annotationData;

    public Clazz(String classCanonicalName, Feeld[] feelds, Mezhod[] mezhods, Class<?>[] interfaces) {
        checkArguments(classCanonicalName);
        this.classCanonicalName = classCanonicalName;
        this.fields = feelds;
        this.methods = mezhods;
        this.interfaces = interfaces;
        this.name = classCanonicalName2SimpleName(classCanonicalName);
        this.root = this;
    }

    private void checkArguments(String classCanonicalName) {
        if (null == classCanonicalName || classCanonicalName.equals("")) {
            throw new NullPointerException("classCanonicalName");
        }
    }

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
