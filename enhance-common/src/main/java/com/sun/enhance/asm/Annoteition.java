package com.sun.enhance.asm;

import org.objectweb.asm.Type;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zksun on 5/12/16.
 */
public class Annoteition implements Serializable {

    private Type type;

    private boolean visible;

    private Map<String, String> values;

    private Class<?> clazz;

    public Annoteition(boolean visible, Type type, Class<?> cls) {
        this.visible = visible;
        this.type = type;
        this.clazz = cls;
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

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Map<String, String> getValues() {
        return values;
    }

    public void setValues(Map<String, String> values) {
        this.values = values;
    }

    public void addValue(String name, String value) {
        if (null == name || name.equals("")) {
            throw new NullPointerException("name");
        }
        synchronized (type) {
            values = null == values ? new HashMap<String, String>() : values;
        }
        values.put(name, value);
    }

    public String getValue(String name) {
        return null == values.get(name) ? "" : values.get(name);
    }

}
