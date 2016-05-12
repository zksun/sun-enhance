package com.sun.enhance.asm;

import org.objectweb.asm.Type;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by zksun on 5/12/16.
 */
public class Feeld implements Serializable {
    private int modifier;
    private String name;
    private Type type;

    private Annoteition[] annotations;

    public Feeld(int modifier, String name, Type type) {
        this.modifier = modifier;
        this.name = name;
        this.type = type;
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
            List<Annoteition> temp = new ArrayList<Annoteition>();
            temp.add(annotation);
            annotations = (Annoteition[]) temp.toArray();
        } else {
            List<Annoteition> temp = Arrays.asList(annotations);
            temp.add(annotation);
            annotations = (Annoteition[]) temp.toArray();
        }
    }

}
