package com.sun.enhance.asm;

import java.io.Serializable;

/**
 * Created by zksun on 5/11/16.
 */
public final class Clazz implements Serializable {

    private String name;
    private Feeld[] fields;
    private Mezhod[] methods;

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


}
