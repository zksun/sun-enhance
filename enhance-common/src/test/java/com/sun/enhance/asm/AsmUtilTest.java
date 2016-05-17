package com.sun.enhance.asm;

import com.sun.enhance.domain.TestClazz;
import com.sun.enhance.logging.Logger;
import com.sun.enhance.logging.LoggerFactory;
import com.sun.enhance.util.ClassUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;

/**
 * Created by zksun on 5/12/16.
 */
public class AsmUtilTest {

    private final static Logger logger = LoggerFactory.getLogger(AsmUtilTest.class);

    @Test
    public void getMethodsTest() {
        Mezhod[] classMethods = AsmUtils.getClassMethods(TestClazz.class.getCanonicalName());
        Assert.assertTrue(null != classMethods);
    }

    @Test
    public void getFieldsTest() {
        Feeld[] classFields = AsmUtils.getClassFeelds(TestClazz.class.getCanonicalName());
        Assert.assertTrue(null != classFields);
    }

    @Test
    public void enhanceClassTest01() {
        URL resource = ClassUtils.getDefaultClassLoader().getResource("com/sun/enhance/domain/TestClazz.class");
        File file = new File(resource.getFile());
        if (file.exists()) {
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                Clazz clazzStructure = AsmUtils.getClazzStructure(fileInputStream, "com.sun.enhance.domain.TestClazz");
                System.out.println(clazzStructure.getSuperName());
                System.out.println(AsmUtils.className2CanonicalName(clazzStructure.getSuperName()));
                System.out.println(clazzStructure);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("end");

    }


}
