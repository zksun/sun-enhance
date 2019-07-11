package com.sun.enhance.asm;

import com.sun.enhance.domain.HelloWorld;
import org.junit.Test;

/**
 * Created by zksun on 2019/7/8.
 */
public class InvokerTest {
    @Test
    public void createInvokerTest() {
        Invoker invoker = InvokerFactory.getInvoker(new HelloWorld());
        Object get = invoker.invoke("getWorld", null);
        System.out.println(get);
    }
}
