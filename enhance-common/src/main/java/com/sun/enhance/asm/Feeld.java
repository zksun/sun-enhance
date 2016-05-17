package com.sun.enhance.asm;

import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by zksun on 5/12/16.
 */
public class Feeld extends AnnotationMemberAdapter {
    private int modifier;
    private Type type;

    public Feeld(int modifier, String name, Type type, Member root) {
        checkArguments(name, type, root);
        this.modifier = modifier;
        this.name = name;
        this.type = type;
        this.root = root;
    }

    public int getModifier() {
        return modifier;
    }

    public void setModifier(int modifier) {
        this.modifier = modifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private void checkArguments(String name, Type type, Member root) {
        if (null == name || name.equals("")) {
            throw new NullPointerException("name");
        }
        if (null == type) {
            throw new NullPointerException("type");
        }
        if (null == root) {
            throw new NullPointerException("root");
        }
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }


}
