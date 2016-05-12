package com.sun.enhance.asm;

import org.objectweb.asm.Type;

import java.io.Serializable;

/**
 * Created by zksun on 5/12/16.
 */
public class Annoteition implements Serializable {

    private Type type;

    private boolean visible;

    private Object value;

    public Annoteition(boolean visible, Type type, Object value) {
        this.visible = visible;
        this.type = type;
        this.value = value;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
