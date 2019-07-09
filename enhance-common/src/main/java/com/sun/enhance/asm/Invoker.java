package com.sun.enhance.asm;

/**
 * Created by zksun on 2019/7/4.
 */
public interface Invoker {
    Object invoke(String methodName, Object[] params);
}
