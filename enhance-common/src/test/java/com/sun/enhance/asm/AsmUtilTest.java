package com.sun.enhance.asm;

import com.sun.enhance.doamin.TestClazz;
import com.sun.enhance.logging.Logger;
import com.sun.enhance.logging.LoggerFactory;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by zksun on 5/12/16.
 */
public class AsmUtilTest {

    private final static Logger logger = LoggerFactory.getLogger(AsmUtilTest.class);

    @Test
    public void getMethodsTest() {
        Mezhod[] classMethods = AsmUtil.getClassMethods(TestClazz.class);
        Assert.assertTrue(null != classMethods);
    }

    @Test
    public void getFieldsTest() {
        Feeld[] classFields = AsmUtil.getClassFeelds(TestClazz.class);
        Assert.assertTrue(null != classFields);
    }
}
