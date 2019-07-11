package com.sun.enhance.domain;

import com.sun.enhance.asm.Invoker;

/**
 * Created by zksun on 15-12-24.
 */
public class Hello implements Invoker {
    private HelloWorld invoker;

    public Hello(HelloWorld something) {
        this.invoker = something;
    }

    public HelloWorld getHelloWorld() {
        return invoker;
    }

    public void setHelloWorld(HelloWorld helloWorld) {
        this.invoker = helloWorld;
    }

    @Override
    public Object invoke(String methodName, Object[] params) {
        if (methodName.equals("getWorld")) {
            return this.invoker.getWorld();
        }
//        else if (methodName.equals("setHello")) {
//            this.setHello((String) params[0]);
//        }
        throw new RuntimeException("no method with this method name");
    }

    private void helloWorld() {
        System.out.println("hello world");
    }
}
