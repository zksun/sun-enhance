package com.sun.enhance.asm;

import org.objectweb.asm.Type;

/**
 * Created by zksun on 5/11/16.
 */
public class Mezhod {
    private int modifiers;
    private String name;
    private Type returnType;
    private Type[] parameterTypes;
    private Type[] exceptionTypes;

    public Mezhod(int modifiers, String name, Type returnType, Type[] parameterTypes, Type[] exceptionTypes) {
        checkArguments(name, returnType);
        this.name = name;
        this.returnType = returnType;
        this.parameterTypes = parameterTypes;
        this.exceptionTypes = exceptionTypes;
        this.modifiers = modifiers;
    }

    private void checkArguments(String name, Type returnType) {
        if (null == name || name.equals("")) {
            throw new NullPointerException("name");
        }
        if (null == returnType) {
            throw new NullPointerException("returnType");
        }
    }

    public int getModifiers() {
        return modifiers;
    }

    public void setModifiers(int modifiers) {
        this.modifiers = modifiers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getReturnType() {
        return returnType;
    }

    public void setReturnType(Type returnType) {
        this.returnType = returnType;
    }

    public Type[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Type[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public Type[] getExceptionTypes() {
        return exceptionTypes;
    }

    public void setExceptionTypes(Type[] exceptionTypes) {
        this.exceptionTypes = exceptionTypes;
    }
}
