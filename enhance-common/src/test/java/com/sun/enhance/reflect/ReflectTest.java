package com.sun.enhance.reflect;

import com.sun.enhance.domain.Hello;
import org.junit.Test;

/**
 * Created by zksun on 2019/7/9.
 */
public class ReflectTest {

    @Test
    public void classScannerTest() {
        ClazzComb clazzDesc = ClassScanner.getInstance().getClazzDesc(Hello.class);
        MethodDesc[] methodDescs = clazzDesc.getMethodDescs();
        for (int i = 0; i < methodDescs.length; i++) {
            MethodDesc methodDesc = methodDescs[i];
            System.out.printf("method name is: %s", methodDesc.getMethodName());
            System.out.println("");
        }

    }
}
