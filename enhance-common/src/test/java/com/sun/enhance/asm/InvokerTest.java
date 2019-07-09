package com.sun.enhance.asm;

import com.sun.enhance.domain.Hello;
import org.junit.Test;

/**
 * Created by zksun on 2019/7/8.
 */
public class InvokerTest {
    @Test
    public void createInvokerTest() {
        Invoker invoker = InvokerFactory.getInvoker(Hello.class, new Hello("something"));
        System.out.println(invoker);
    }
}
