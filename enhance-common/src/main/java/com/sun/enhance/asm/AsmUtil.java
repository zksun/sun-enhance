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

    static void executeMethodAdapter(ClassAdapter adapter, String classCanonicalName) {
        if (null == adapter) {
            throw new NullPointerException("adapter");
        }
        if (null == classCanonicalName || classCanonicalName.equals("")) {
            throw new NullPointerException("classCanonicalName");
        }

        try {
            ClassReader cr = new ClassReader(classCanonicalName);
            cr.accept(adapter, ClassReader.SKIP_DEBUG);
        } catch (Throwable throwable) {
            logger.error("get classWriter class object failure for class: {}", classCanonicalName, throwable);
        }

    }

    static void executeFieldAdapter(ClazzAdapter adapter, String classCanonicalName) {
        if (null == adapter) {
            throw new NullPointerException("adapter");
        }
        if (null == classCanonicalName || classCanonicalName.equals("")) {
            throw new NullPointerException("classCanonicalName");
        }

        try {
            ClassReader cr = new ClassReader(classCanonicalName);
            cr.accept(adapter, ClassReader.SKIP_DEBUG);
        } catch (Throwable throwable) {
            logger.error("get classWriter class object failure for class: {}", classCanonicalName, throwable);
        }
    }


    static Mezhod[] getClassMethods(String classCanonicalName) {
        if (null == classCanonicalName || classCanonicalName.equals("")) {
            throw new NullPointerException("classCanonicalName");
        }
        try {
            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            ClazzAdapter classAdapter = new ClazzAdapter(classWriter);
            executeMethodAdapter(classAdapter, classCanonicalName);
            return classAdapter.getMezhods();
        } catch (Throwable throwable) {
            logger.error("execute classAdapter class object failure for class: {}", classCanonicalName, throwable);
        }
        return null;
    }

    static Feeld[] getClassFeelds(String classCanonicalName) {
        if (null == classCanonicalName || classCanonicalName.equals("")) {
            throw new NullPointerException("classCanonicalName");
        }
        try {
            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            ClazzAdapter classAdapter = new ClazzAdapter(classWriter);
            executeFieldAdapter(classAdapter, classCanonicalName);
            return classAdapter.getFeelds();
        } catch (Throwable throwable) {
            logger.error("execute classAdapter class object failure for class: {}", classCanonicalName, throwable);
        }
        return null;
    }

    static Class<?>[] getInterfaces(String[] interfaces) {
        if (null == interfaces || interfaces.length < 1) {
            throw new NullPointerException("interfaces");
        }

        Class<?>[] classes = new Class<?>[interfaces.length];

        for (int i = 0; i < interfaces.length; i++) {
            try {
                classes[i] = Class.forName(interfaces[0].replace('/', '.'));
            } catch (ClassNotFoundException e) {
                continue;
            }
        }
        return classes;
    }

    private static class ClazzAdapter extends ClassAdapter {

        private List<Mezhod> mezhodList = new ArrayList<Mezhod>();

        private List<Feeld> feeldList = new ArrayList<Feeld>();

        private Feeld _curFeeld;

        /**
         * Constructs a new {@link ClassAdapter} object.
         *
         * @param cv the class visitor to which this adapter must delegate calls.
         */
        public ClazzAdapter(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {

        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            Mezhod method;
            if (null != (method = createMethod(access, name, desc, exceptions))) {
                mezhodList.add(method);
            }
            return super.visitMethod(access, name, desc, signature, exceptions);
        }

        @Override
        public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            System.out.println(desc);
            return super.visitAnnotation(desc, visible);
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
                return mezhodList.toArray(new Mezhod[mezhodList.size()]);
            }
            return null;
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
                return feeldList.toArray(new Feeld[feeldList.size()]);
            }
            return null;
        }

        private Feeld createFeeld(int modifier, String name, String desc) {
            return new Feeld(modifier, name, Type.getType(desc));
        }

        private abstract class abstractAnnotationAdapter implements AnnotationVisitor {
            @Override
            public void visit(String name, Object value) {

            }

            @Override
            public void visitEnum(String name, String desc, String value) {

            }

            @Override
            public AnnotationVisitor visitAnnotation(String name, String desc) {
                return null;
            }

            @Override
            public AnnotationVisitor visitArray(String name) {
                return null;
            }

            @Override
            public void visitEnd() {

            }
        }

        private class FieldAnnotationAdapter implements FieldVisitor {
            @Override
            public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
                final Annoteition annotation;
                try {
                    if (null != (annotation = createAnnoteition(desc, visible))) {
                        setAnnotation(annotation);
                    }
                } catch (Exception e) {
                    logger.error("parser annotation error with desc: {} ", desc, e);
                    throw new RuntimeException(e);
                }
                return new abstractAnnotationAdapter() {
                    @Override
                    public void visit(String name, Object value) {
                        annotation.addValue(name, value.toString());
                    }
                };
            }

            @Override
            public void visitAttribute(Attribute attr) {
                //do nothing
            }

            @Override
            public void visitEnd() {
                //do nothing
            }

            private Annoteition createAnnoteition(String desc, boolean visible) throws ClassNotFoundException {
                return new Annoteition(visible, Type.getType(desc), Class.forName(Type.getType(desc).getClassName()));
            }

            private synchronized void setAnnotation(Annoteition annotation) {
                if (null != _curFeeld) {
                    _curFeeld.addAnnotation(annotation);
                    _curFeeld = null;
                }
            }
        }

    }
}
