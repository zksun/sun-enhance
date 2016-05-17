package com.sun.enhance.asm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by zksun on 5/17/16.
 */
public abstract class AnnotationMemberAdapter extends Member {
    private Annoteition[] annotations;

    public Annoteition[] getAnnotations() {
        return annotations;
    }

    public void setAnnotations(Annoteition[] annotations) {
        this.annotations = annotations;
    }

    protected synchronized void addAnnotation(Annoteition annotation) {
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
