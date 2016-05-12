package com.sun.enhance.asm;

import com.sun.enhance.logging.Logger;
import com.sun.enhance.logging.LoggerFactory;
import com.sun.enhance.util.CollectionUtils;
import org.objectweb.asm.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zksun on 5/11/16.
 */
public final class AsmUtil {

    private static final Logger logger = LoggerFactory.getLogger(AsmUtil.class);

    final static String CLINIT = "<clinit>";
    final static String INIT = "<init>";

    static void writeDefaultInit(ClassWriter clsWriter) {
        MethodVisitor mw = clsWriter.visitMethod(Opcodes.ACC_PUBLIC, INIT,
                getVoidMethodDescriptor(), null, null);
        mw.visitVarInsn(Opcodes.ALOAD, 0);
        mw.visitMethodInsn(Opcodes.INVOKESPECIAL, Type.getType(Object.class).getInternalName(), INIT, getVoidMethodDescriptor());
        mw.visitInsn(Opcodes.RETURN);
        mw.visitMaxs(0, 0);
        mw.visitEnd();
    }

    static String getVoidMethodDescriptor() {
        return Type.getMethodDescriptor(Type.VOID_TYPE, new Type[]{});
    }

    static ClassWriter createClassByBaseClass(String className) throws IOException {
        ClassReader classReader = new ClassReader(className);
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classReader.accept(classWriter, ClassReader.SKIP_DEBUG);
        return classWriter;
    }

    static void executeMethodAdapter(ClassAdapter adapter, Class<?> cls) {
        if (null == adapter) {
            throw new NullPointerException("adapter");
        }
        if (null == cls) {
            throw new NullPointerException("cls");
        }

        try {
            ClassReader cr = new ClassReader(cls.getCanonicalName());
            cr.accept(adapter, ClassReader.SKIP_DEBUG);
        } catch (Throwable throwable) {
            logger.error("get classWriter class object failure for class: {}", cls.getName(), throwable);
        }

    }

    static void executeFieldAdapter(FeeldAdapter adapter, Class<?> cls) {
        if (null == adapter) {
            throw new NullPointerException("adapter");
        }
        if (null == cls) {
            throw new NullPointerException("cls");
        }

        try {
            ClassReader cr = new ClassReader(cls.getCanonicalName());
            cr.accept(adapter, ClassReader.SKIP_DEBUG);
        } catch (Throwable throwable) {
            logger.error("get classWriter class object failure for class: {}", cls.getName(), throwable);
        }
    }


    public static Mezhod[] getClassMethods(Class<?> cls) {
        if (null == cls) {
            throw new NullPointerException("cls");
        }
        try {
            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            MezhodAdapter classAdapter = new MezhodAdapter(classWriter);
            executeMethodAdapter(classAdapter, cls);
            return classAdapter.getMezhods();
        } catch (Throwable throwable) {
            logger.error("execute classAdapter class object failure for class: {}", cls.getName(), throwable);
        }
        return null;
    }

    public static Feeld[] getClassFeelds(Class<?> cls) {
        if (null == cls) {
            throw new NullPointerException("cls");
        }
        try {
            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            FeeldAdapter classAdapter = new FeeldAdapter(classWriter);
            executeFieldAdapter(classAdapter, cls);
            return classAdapter.getFeelds();
        } catch (Throwable throwable) {
            logger.error("execute classAdapter class object failure for class: {}", cls.getName(), throwable);
        }
        return null;
    }

    private static class FeeldAdapter extends ClassAdapter {

        private List<Feeld> feeldList = new ArrayList<Feeld>();

        private Feeld _curFeeld;

        /**
         * Constructs a new {@link ClassAdapter} object.
         *
         * @param cv the class visitor to which this adapter must delegate calls.
         */
        public FeeldAdapter(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
            Feeld feeld;
            if (null != (feeld = createFeeld(access, name, desc))) {
                _curFeeld = feeld;
                feeldList.add(feeld);
            }
            return new FieldAnnotationAdapter();
        }

        public Feeld[] getFeelds() {
            if (CollectionUtils.isNotEmpty(feeldList)) {
                return (Feeld[]) feeldList.toArray();
            }
            return null;
        }

        private Feeld createFeeld(int modifier, String name, String desc) {
            return new Feeld(modifier, name, Type.getType(desc));
        }


        private class FieldAnnotationAdapter implements FieldVisitor {
            @Override
            public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
                System.out.println(String.format("annotation: {%s}", desc));
                Annoteition annotation;
                if (null != (annotation = createAnnoteition(desc, visible))) {
                    setAnnotation(annotation);
                }
                return null;
            }

            @Override
            public void visitAttribute(Attribute attr) {
                //do nothing
            }

            @Override
            public void visitEnd() {
                //do nothing
            }

            private Annoteition createAnnoteition(String desc, boolean visible) {
                return new Annoteition();
            }

            private void setAnnotation(Annoteition annotation) {
                if (null != _curFeeld) {

                }
            }
        }

    }

    private static class MezhodAdapter extends ClassAdapter {

        private List<Mezhod> mezhodList = new ArrayList<Mezhod>();

        /**
         * Constructs a new {@link ClassAdapter} object.
         *
         * @param cv the class visitor to which this adapter must delegate calls.
         */
        public MezhodAdapter(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            Mezhod method;
            if (null != (method = createMethod(access, name, desc, exceptions))) {
                mezhodList.add(method);
            }
            return super.visitMethod(access, name, desc, signature, exceptions);
        }

        private Mezhod createMethod(int access, String name, String desc, String[] exceptions) {
            return new Mezhod(access, name, Type.getReturnType(desc), Type.getArgumentTypes(desc), getExceptions(exceptions));
        }

        private Type[] getExceptions(String[] exceptions) {
            if (null == exceptions || exceptions.length < 1) {
                return null;
            }
            Type[] result = new Type[exceptions.length];

            for (int i = 0; i < exceptions.length; i++) {
                result[i] = Type.getObjectType(exceptions[i].trim());
            }
            return result;
        }

        public Mezhod[] getMezhods() {
            if (CollectionUtils.isNotEmpty(mezhodList)) {
                return (Mezhod[]) mezhodList.toArray();
            }
            return null;
        }
    }
}
