package com.sun.enhance.domain;

import com.sun.enhance.asm.Invoker;

/**
 * Created by zksun on 15-12-24.
 */
public class Hello implements Invoker {
    private String hello;

    public Hello(String something) {
        this.hello = something;
    }

    public String getHello() {
        return hello;
    }

    public void setHello(String hello) {
        this.hello = hello;
    }

    @Override
    public Object invoke(String methodName, Object[] params) {
        if (methodName.equals("getHello")) {
            return this.getHello();
        } else if (methodName.equals("setHello")) {
            this.setHello((String) params[0]);
        }
        throw new RuntimeException("no method with this method name");
    }
}
