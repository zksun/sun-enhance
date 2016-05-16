package com.sun.enhance.asm;

import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by zksun on 5/12/16.
 */
public class Feeld extends Member {
    private int modifier;
    private Type type;

    private Annoteition[] annotations;

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

    public Annoteition[] getAnnotations() {
        return annotations;
    }

    public void setAnnotations(Annoteition[] annotations) {
        this.annotations = annotations;
    }

    public synchronized void addAnnotation(Annoteition annotation) {
        if (null == annotations) {
            List<Annoteition> temp = new ArrayList<Annoteition>(1);
            temp.add(annotation);
            annotations = temp.toArray(new Annoteition[1]);
        } else {
            List<Annoteition> temp = Arrays.asList(annotations);
            temp.add(annotation);
            annotations = temp.toArray(new Annoteition[temp.size()]);
        }
    }

}
