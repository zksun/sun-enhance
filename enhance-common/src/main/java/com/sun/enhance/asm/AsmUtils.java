package com.sun.enhance.asm;

import com.sun.enhance.NestedIOException;
import com.sun.enhance.logging.Logger;
import com.sun.enhance.logging.LoggerFactory;
import com.sun.enhance.util.CollectionUtils;
import com.sun.enhance.util.StringUtils;
import org.objectweb.asm.*;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.ElementType;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zksun on 5/11/16.
 */
public final class AsmUtils {

    private static final Logger logger = LoggerFactory.getLogger(AsmUtils.class);

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

    static void executeClazzAdapter(ClassAdapter adapter, InputStream inputStream) {
        if (null == adapter) {
            throw new NullPointerException("adapter");
        }
        if (null == inputStream) {
            throw new NullPointerException("classCanonicalName");
        }

        try {
            ClassReader cr = new ClassReader(inputStream);
            cr.accept(adapter, ClassReader.SKIP_DEBUG);
        } catch (Throwable throwable) {
            logger.error("get classWriter class object failure", throwable);
        }

    }

    static void executeClazzAdapter(ClassAdapter adapter, String classCanonicalName) {
        if (null == adapter) {
            throw new NullPointerException("adapter");
        }
        if (StringUtils.isEmpty(classCanonicalName)) {
            throw new NullPointerException("classCanonicalName");
        }

        try {
            ClassReader cr = new ClassReader(classCanonicalName);
            cr.accept(adapter, ClassReader.SKIP_DEBUG);
        } catch (Throwable throwable) {
            logger.error("get classWriter class object failure for class: {}", classCanonicalName, throwable);
        }

    }

    static Mezhod[] getClassMethods(InputStream inputStream, String classCanonicalName) {
        if (null == inputStream) {
            throw new NullPointerException("classInputStream");
        }
        try {
            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            ClazzAdapter classAdapter = new ClazzAdapter(new Clazz(classCanonicalName, null, null, null), classWriter);
            executeClazzAdapter(classAdapter, inputStream);
            return classAdapter.getMezhods();
        } catch (Throwable throwable) {
            logger.error("execute classAdapter class object failure for class: {}", classCanonicalName, throwable);
        }
        return EmptyElement.EMPTY_MEZHOD;
    }


    static Mezhod[] getClassMethods(String classCanonicalName) {
        if (StringUtils.isEmpty(classCanonicalName)) {
            throw new NullPointerException("classCanonicalName");
        }
        try {
            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            ClazzAdapter classAdapter = new ClazzAdapter(new Clazz(classCanonicalName, null, null, null), classWriter);
            executeClazzAdapter(classAdapter, classCanonicalName);
            return classAdapter.getMezhods();
        } catch (Throwable throwable) {
            logger.error("execute classAdapter class object failure for class: {}", classCanonicalName, throwable);
        }
        return EmptyElement.EMPTY_MEZHOD;
    }

    static Feeld[] getClassFeelds(InputStream inputStream, String classCanonicalName) {
        if (null == inputStream) {
            throw new NullPointerException("classInputStream");
        }
        try {
            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            ClazzAdapter classAdapter = new ClazzAdapter(new Clazz(classCanonicalName, null, null, null), classWriter);
            executeClazzAdapter(classAdapter, inputStream);
            return classAdapter.getFeelds();
        } catch (Throwable throwable) {
            logger.error("execute classAdapter class object failure for class: {}", classCanonicalName, throwable);
        }
        return EmptyElement.EMPTY_FEELDS;
    }

    static Feeld[] getClassFeelds(String classCanonicalName) {
        if (StringUtils.isEmpty(classCanonicalName)) {
            throw new NullPointerException("classCanonicalName");
        }
        try {
            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            ClazzAdapter classAdapter = new ClazzAdapter(new Clazz(classCanonicalName, null, null, null), classWriter);
            executeClazzAdapter(classAdapter, classCanonicalName);
            return classAdapter.getFeelds();
        } catch (Throwable throwable) {
            logger.error("execute classAdapter class object failure for class: {}", classCanonicalName, throwable);
        }
        return EmptyElement.EMPTY_FEELDS;
    }

    static String classCanonicalName2SimpleName(String canonicalName) {
        String names[] = canonicalName.split("\\.");
        return names[names.length - 1];
    }

    static String className2CanonicalName(String name) {
        return name.replace('/', '.');
    }

    public static Clazz getClazzStructure(InputStream inputStream, String classCanonicalName) throws NestedIOException {
        if (null == inputStream) {
            throw new NullPointerException("inputStream");
        }
        ClazzAdapter clazzAdapter = new ClazzAdapter(new Clazz(classCanonicalName, null, null, null), new ClassWriter(ClassWriter.COMPUTE_MAXS));
        executeClazzAdapter(clazzAdapter, inputStream);
        return clazzAdapter.getClazzDefinition();
    }

    public static Clazz getClazzStructure(String classCanonicalName) throws NestedIOException {
        if (StringUtils.isEmpty(classCanonicalName)) {
            throw new NullPointerException("classCanonicalName");
        }
        ClazzAdapter clazzAdapter = new ClazzAdapter(new Clazz(classCanonicalName, null, null, null), new ClassWriter(ClassWriter.COMPUTE_MAXS));
        executeClazzAdapter(clazzAdapter, classCanonicalName);
        return clazzAdapter.getClazzDefinition();
    }

    private static class ClazzAdapter extends ClassAdapter {

        private List<Mezhod> mezhodList = new ArrayList<Mezhod>();

        private List<Feeld> feeldList = new ArrayList<Feeld>();

        private Feeld _curFeeld;

        private Mezhod _curMezhod;

        private final Clazz _self;

        public ClazzAdapter(Clazz self, ClassVisitor cv) {
            super(cv);
            checkArguments(self, cv);
            this._self = self;
        }

        private void checkArguments(Clazz self, ClassVisitor cv) {
            if (null == self) {
                throw new NullPointerException("self");
            }
            if (null == cv) {
                throw new NullPointerException("class visitor");
            }
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            _self.setSuperName(superName);
            _self.setName(name);
            _self.setModifiers(access);
            _self.setVersion(version);
            if (null != interfaces && interfaces.length > 0) {
                _self.setInterfaces(getInterfaces(interfaces));
            }
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            Mezhod method;
            if (name.equals(INIT) || Modifier.isNative(access)) {
                return super.visitMethod(access, name, desc, signature, exceptions);
            }
            if ((null != (method = createMethod(access, name, desc, exceptions)))) {
                _curMezhod = method;
                mezhodList.add(method);
            }
            return new MethodAnnotationAdapter(super.visitMethod(access, name, desc, signature, exceptions));
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

        @Override
        public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            System.out.println(desc);
            return super.visitAnnotation(desc, visible);
        }

        private Class<?>[] getInterfaces(String[] interfaces) {
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

        private Mezhod createMethod(int access, String name, String desc, String[] exceptions) {
            return new Mezhod(access, name, Type.getReturnType(desc), Type.getArgumentTypes(desc), getExceptions(exceptions), this._self);
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

        Mezhod[] getMezhods() {
            if (CollectionUtils.isNotEmpty(mezhodList)) {
                return mezhodList.toArray(new Mezhod[mezhodList.size()]);
            }
            return null;
        }

        Feeld[] getFeelds() {
            if (CollectionUtils.isNotEmpty(feeldList)) {
                return feeldList.toArray(new Feeld[feeldList.size()]);
            }
            return null;
        }

        public Clazz getClazzDefinition() throws NestedIOException {
            if (null != _self) {
                if (CollectionUtils.isNotEmpty(this.feeldList)) {
                    _self.setFields(feeldList.toArray(new Feeld[feeldList.size()]));
                }
                if (CollectionUtils.isNotEmpty(this.mezhodList)) {
                    _self.setMethods(mezhodList.toArray(new Mezhod[feeldList.size()]));
                }
                return _self;
            }
            throw new NestedIOException("class analysis failure");
        }

        private Feeld createFeeld(int modifier, String name, String desc) {
            return new Feeld(modifier, name, Type.getType(desc), this._self);
        }

        private abstract class AbstractAnnotationAdapter implements AnnotationVisitor {
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

        private Annoteition createAnnoteition(String desc, boolean visible, ElementType elementType) throws ClassNotFoundException {
            return new Annoteition(visible, Type.getType(desc), Class.forName(Type.getType(desc).getClassName()), elementType, ClazzAdapter.this._self);
        }

        private class MethodAnnotationAdapter extends MethodAdapter {

            /**
             * Constructs a new {@link MethodAdapter} object.
             *
             * @param mv the code visitor to which this adapter must delegate calls.
             */
            public MethodAnnotationAdapter(MethodVisitor mv) {
                super(mv);
            }

            @Override
            public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
                final Annoteition annotation;
                try {
                    if (null != (annotation = createAnnoteition(desc, visible, ElementType.FIELD))) {
                        setAnnotation(annotation);
                    }
                } catch (Exception e) {
                    logger.error("parser annotation error with desc: {} ", desc, e);
                    throw new RuntimeException(e);
                }
                return new AbstractAnnotationAdapter() {
                    @Override
                    public void visit(String name, Object value) {
                        if (!StringUtils.isEmpty(name)) {
                            annotation.addValue(name, value.toString());
                        }
                    }
                };
            }

            private synchronized void setAnnotation(Annoteition annotation) {
                if (null != _curMezhod) {
                    _curMezhod.addAnnotation(annotation);
                    _curMezhod = null;
                }
            }
        }

        private class FieldAnnotationAdapter implements FieldVisitor {
            @Override
            public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
                final Annoteition annotation;
                try {
                    if (null != (annotation = createAnnoteition(desc, visible, ElementType.FIELD))) {
                        setAnnotation(annotation);
                    }
                } catch (Exception e) {
                    logger.error("parser annotation error with desc: {} ", desc, e);
                    throw new RuntimeException(e);
                }
                return new AbstractAnnotationAdapter() {
                    @Override
                    public void visit(String name, Object value) {
                        if (!StringUtils.isEmpty(name)) {
                            annotation.addValue(name, value.toString());
                        }
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

            private synchronized void setAnnotation(Annoteition annotation) {
                if (null != _curFeeld) {
                    _curFeeld.addAnnotation(annotation);
                    _curFeeld = null;
                }
            }
        }

    }
}
