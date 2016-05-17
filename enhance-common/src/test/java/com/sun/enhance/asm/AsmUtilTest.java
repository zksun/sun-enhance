package com.sun.enhance.asm;

import com.sun.enhance.domain.SayHello;
import com.sun.enhance.domain.TestClazz;
import com.sun.enhance.logging.Logger;
import com.sun.enhance.logging.LoggerFactory;
import com.sun.enhance.util.ClassUtils;
import com.sun.enhance.util.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.objectweb.asm.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;

import static org.objectweb.asm.Opcodes.*;

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

                ClassWriter classWriter = AsmUtils.createClassByBaseClass(clazzStructure.getClassCanonicalName());
                classWriter.visit(Opcodes.V1_6, Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER,
                        clazzStructure.getSimpleName(), null, Type.getType(Object.class).getInternalName(),
                        new String[]{Type.getType(String.class).getInternalName()});

                MethodVisitor methodVisitor = classWriter.visitMethod(Opcodes.ACC_PUBLIC, "testMethod",
                        Type.getMethodDescriptor(Type.VOID_TYPE, new Type[]{Type.getType(String.class)}), null, null);

                methodVisitor.visitFieldInsn(GETSTATIC, "java/lang/System", "out",
                        "Ljava/io/PrintStream;");
                methodVisitor.visitLdcInsn("Hello world!");
                methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println",
                        "(Ljava/lang/String;)V");

                methodVisitor.visitInsn(RETURN);
                methodVisitor.visitMaxs(2, 2);
                methodVisitor.visitEnd();

                byte[] bytes = classWriter.toByteArray();

                IOUtils.write(bytes, new FileOutputStream("/Users/hanshou/Downloads/sun-enhance/enhance-common/target/test-classes/com/sun/enhance/domain/" + clazzStructure.getSimpleName() + ".class"));

                TestClazz testClazz = new TestClazz();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("end");

    }

    @Test
    public void enhanceClassTest02() {
        URL resource = ClassUtils.getDefaultClassLoader().getResource("com/sun/enhance/domain/SayHello.class");
        File file = new File(resource.getFile());
        if (file.exists()) {
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                Clazz clazzStructure = AsmUtils.getClazzStructure(fileInputStream, "com.sun.enhance.domain.SayHello");

                ClassReader classReader = new ClassReader(clazzStructure.getClassCanonicalName());
                ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
                ClassAdapter classAdapter = new GeneralClassAdapter(classWriter);
                classReader.accept(classAdapter, 0);
                byte[] bytes = classWriter.toByteArray();
                ClassUtils.classRedefine(clazzStructure.getClassCanonicalName(), bytes);
                SayHello hello = new SayHello();
                hello.sayHello();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("end");

    }

    static class GeneralClassAdapter extends ClassAdapter {

        /**
         * Constructs a new {@link ClassAdapter} object.
         *
         * @param cv the class visitor to which this adapter must delegate calls.
         */
        public GeneralClassAdapter(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals("sayHello")) {
                MethodVisitor newMv = new SayHelloMethod(methodVisitor);
                return newMv;
            } else {
                return methodVisitor;
            }
        }

        @Override
        public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
            return super.visitField(access, name, desc, signature, value);
        }

    }

    static class SayHelloMethod extends MethodAdapter {
        public SayHelloMethod(MethodVisitor mv) {
            super(mv);
        }

        @Override
        public void visitCode() {
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitLdcInsn("hello world");
            mv.visitFieldInsn(Opcodes.PUTFIELD, "com/sun/enhance/domain/SayHello", "name", Type.getDescriptor(String.class));

            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "com/sun/enhance/domain/SayHello", "name", "Ljava/lang/String;");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V");
        }


    }


}
