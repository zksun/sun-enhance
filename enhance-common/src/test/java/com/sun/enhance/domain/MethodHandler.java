package com.sun.enhance.domain;

/**
 * Created by zksun on 2019/7/2.
 */
public class MethodHandler {
    private final SayHello sayHello;

    public MethodHandler() {
        sayHello = new SayHello();
    }

    public <T> T invoke(String methodName) {
        if (methodName.equals("sayHello")) {
            this.sayHello.sayHello();
            return (T) "void";
        } else if (methodName.equals("sayHi")) {
            this.sayHello.sayHi();
            return (T) "void";
        } else {
            throw new IllegalArgumentException("method without this name");
        }
    }


}
