package com.sun.enhance.util;

import org.junit.Test;

/**
 * Created by zksun on 5/16/16.
 */
public class SystemUtilTest {

    @Test
    public void systemPropertiesTest() {
        System.out.println(ClassUtils.getClassPath());
        System.out.println(ClassUtils.getUserDirPath());
    }

}
