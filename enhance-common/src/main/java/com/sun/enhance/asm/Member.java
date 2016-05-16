package com.sun.enhance.asm;

import org.objectweb.asm.Type;

import java.io.Serializable;

/**
 * Created by hanshou on 5/16/16.
 */
public abstract class Member implements Serializable {
    String name;
    Member root;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Member getRoot() {
        return root;
    }

    public void setRoot(Member root) {
        this.root = root;
    }
}
