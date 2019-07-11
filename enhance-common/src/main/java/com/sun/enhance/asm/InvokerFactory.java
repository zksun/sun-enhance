package com.sun.enhance.asm;

import com.sun.enhance.reflect.ClassScanner;
import com.sun.enhance.reflect.ClazzComb;
import com.sun.enhance.reflect.MethodDesc;
import com.sun.enhance.util.IOUtils;
import org.objectweb.asm.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashMap;

import static org.objectweb.asm.Opcodes.*;

/**
 * Created by zksun on 2019/7/4.
 */
public class InvokerFactory {

    private ASMClassLoader classLoader = new ASMClassLoader();

    private final String[] INTERFACE = new String[]{getType(Invoker.class)};

    private final static InvokerFactory instance = new InvokerFactory();

    private final HashMap<Class<?>, Invoker> invokerHashMap;

    private InvokerFactory() {
        invokerHashMap = new HashMap<>();
    }

    private Invoker innerGetInvoker(Class<?> clazz, Object source) {
        Invoker invoker = invokerHashMap.get(clazz);
        if (null == invoker) {
            synchronized (invokerHashMap) {
                invoker = invokerHashMap.get(clazz);
                if (null == invoker) {
                    invoker = createInvoker(clazz, source);
                    invokerHashMap.put(clazz, invoker);
                    return invoker;
                }
            }
        }
        return invoker;
    }

    private Invoker createInvoker(final Class<?> clazz, final Object source) {
        if (clazz.isEnum()) {
            throw new UnsupportedOperationException("do not support enum type");
        }

        ClassWriter clsWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        clsWriter.visit(Opcodes.V1_6, Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER,
                getClassName(clazz), "java/lang/Object", "java/lang/Object", INTERFACE);
        createInvokeSource(clazz, clsWriter);
        createInit(clsWriter, clazz);
        createInvokeMethod(clsWriter, clazz);

        byte[] code = clsWriter.toByteArray();

        try {
            IOUtils.write(clsWriter.toByteArray(), new FileOutputStream("/Users/hanshou/Documents/work/feature/sun-enhance/enhance-common/target/test-classes/com/sun/enhance/domain/" + getClassName(clazz) + ".class"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Class<?> exampleClass = classLoader.defineClassPublic(getClassName(clazz), code,
                0, code.length);
        Object instance = null;

        try {
            Constructor<?> declaredConstructor = exampleClass.getDeclaredConstructor(new Class[]{clazz});
            instance = declaredConstructor.newInstance(source);
            return (Invoker) instance;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("can not create invoker");
        }
    }

    private String getClassName(Class<?> clazz) {
        String className = clazz.getCanonicalName();
        className = className.replace('.', '_');
        className += "_asm_invoker";
        return className;
    }

    private String getPrimitiveLetter(Class<?> type) {
        if (Integer.TYPE.equals(type)) {
            return "I";
        } else if (Void.TYPE.equals(type)) {
            return "V";
        } else if (Boolean.TYPE.equals(type)) {
            return "Z";
        } else if (Character.TYPE.equals(type)) {
            return "C";
        } else if (Byte.TYPE.equals(type)) {
            return "B";
        } else if (Short.TYPE.equals(type)) {
            return "S";
        } else if (Float.TYPE.equals(type)) {
            return "F";
        } else if (Long.TYPE.equals(type)) {
            return "J";
        } else if (Double.TYPE.equals(type)) {
            return "D";
        }

        throw new IllegalStateException("Type: " + type.getCanonicalName()
                + " is not a primitive type");
    }

    private String getType(Class<?> type) {
        if (type.isArray()) {
            return "[" + getDesc(type.getComponentType());
        } else {
            if (!type.isPrimitive()) {
                String clsName = type.getCanonicalName();

                if (type.isMemberClass()) {
                    int lastDot = clsName.lastIndexOf(".");
                    clsName = clsName.substring(0, lastDot) + "$"
                            + clsName.substring(lastDot + 1);
                }
                return clsName.replaceAll("\\.", "/");
            } else {
                return getPrimitiveLetter(type);
            }
        }
    }

    private String getDesc(Class<?> type) {
        if (type.isPrimitive()) {
            return getPrimitiveLetter(type);
        } else if (type.isArray()) {
            return "[" + getDesc(type.getComponentType());
        } else {
            return "L" + getType(type) + ";";
        }
    }

    private String constructMethodDesc(Class<?> returnType,
                                       Class<?>... paramType) {
        StringBuilder methodDesc = new StringBuilder();
        methodDesc.append('(');
        for (int i = 0; i < paramType.length; i++) {
            methodDesc.append(getDesc(paramType[i]));
        }
        methodDesc.append(')');
        if (returnType == Void.class) {
            methodDesc.append("V");
        } else {
            methodDesc.append(getDesc(returnType));
        }
        return methodDesc.toString();
    }

    private void createInvokeSource(Class<?> clazz, ClassWriter classWriter) {
        FieldVisitor fdVisitor = classWriter
                .visitField(Opcodes.ACC_PRIVATE | Opcodes.ACC_FINAL,
                        "invoker",
                        getDesc(clazz),
                        null, null);
        fdVisitor.visitEnd();
    }

    private void createInit(ClassWriter classWriter, Class<?> clazz) {
        MethodVisitor mw = classWriter.visitMethod(Opcodes.ACC_PUBLIC, "<init>",
                constructMethodDesc(Void.class, new Class<?>[]{clazz}), null, null);

        mw.visitCode();

        Label l0 = new Label();
        mw.visitLabel(l0);
        mw.visitVarInsn(ALOAD, 0);
        mw.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V");

        Label l1 = new Label();
        mw.visitLabel(l1);
        mw.visitVarInsn(ALOAD, 0);
        mw.visitVarInsn(ALOAD, 1);
        mw.visitFieldInsn(PUTFIELD, getClassName(clazz), "invoker", getDesc(clazz));

        Label l2 = new Label();
        mw.visitLabel(l2);
        mw.visitInsn(RETURN);

        Label l3 = new Label();
        mw.visitLabel(l3);
        mw.visitLocalVariable("this", getDesc(clazz), null, l0, l2, 0);
        mw.visitLocalVariable("invoker", getDesc(clazz), null, l0, l2, 1);

        mw.visitMaxs(2, 2);
        mw.visitEnd();
    }

    private void createInvokeMethod(ClassWriter classWriter, Class<?> clazz) {
        MethodVisitor mw = classWriter.visitMethod(ACC_PUBLIC,
                "invoke", "(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;", null, null);
        mw.visitCode();

        createPublicMethodsProxy(mw, clazz);

        mw.visitMaxs(1, 1);
        mw.visitEnd();

    }

    private void createMethodInvoke(MethodVisitor mw, MethodDesc methodDesc, Class<?> clazz, boolean frame) {
        if (frame) {
            mw.visitFrame(F_SAME, 0, null, 0, null);
        }
        mw.visitVarInsn(ALOAD, 1);
        mw.visitLdcInsn(methodDesc.getMethodName());
        mw.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z");
        Label ifeq = new Label();
        mw.visitJumpInsn(IFEQ, ifeq);

        Label branch = new Label();
        mw.visitLabel(branch);
        mw.visitVarInsn(ALOAD, 0);
        mw.visitFieldInsn(GETFIELD, getClassName(clazz), "invoker", getDesc(clazz));
        mw.visitMethodInsn(INVOKEVIRTUAL, getType(clazz)
                , methodDesc.getMethodName(),
                Type.getMethodDescriptor(methodDesc.getSource()));

        createMethodParams(mw, methodDesc);

        if (methodDesc.returnType().equals(Void.TYPE)) {
            mw.visitInsn(ACONST_NULL);
        }
        mw.visitInsn(ARETURN);
        mw.visitLabel(ifeq);

    }

    private void createMethodParams(MethodVisitor mw, MethodDesc methodDesc) {
        if (!methodDesc.hasParameter()) {
            return;
        }
        mw.visitVarInsn(ALOAD, 0);
        Class<?>[] classes = methodDesc.parameterTypes();
        for (int i = 0; i < classes.length; i++) {
            Class<?> aClass = classes[i];
            mw.visitVarInsn(ALOAD, 2);

            if (i == 0) {
                mw.visitInsn(ICONST_0);
            } else if (i == 1) {
                mw.visitInsn(ICONST_1);
            } else if (i == 2) {
                mw.visitInsn(ICONST_2);
            } else if (i == 3) {
                mw.visitInsn(ICONST_3);
            } else if (i == 4) {
                mw.visitInsn(ICONST_4);
            } else if (i == 5) {
                mw.visitInsn(ICONST_5);
            } else {
                mw.visitIntInsn(BIPUSH, i);
            }

            mw.visitInsn(AALOAD);
            mw.visitTypeInsn(CHECKCAST, getType(aClass));
        }
    }

    private void createPublicMethodsProxy(MethodVisitor mw, Class<?> clazz) {
        Label start = new Label();
        mw.visitLabel(start);

        ClazzComb clazzDesc = ClassScanner.getInstance().getClazzDesc(clazz);
        MethodDesc[] methodDescs = clazzDesc.getMethodDescs();
        if (methodDescs.length > 0) {
            for (int i = 0; i < methodDescs.length; i++) {
                MethodDesc methodDesc = methodDescs[i];
                if (i == 0) {
                    createMethodInvoke(mw, methodDesc, clazz, false);
                } else {
                    createMethodInvoke(mw, methodDesc, clazz, true);
                }
            }
            mw.visitFrame(F_SAME, 0, null, 0, null);
            mw.visitTypeInsn(NEW, "java/lang/RuntimeException");
            mw.visitInsn(DUP);
            mw.visitLdcInsn("no method with this method name");
            mw.visitMethodInsn(INVOKESPECIAL, "java/lang/RuntimeException", "<init>", "(Ljava/lang/String;)V");
            mw.visitInsn(ATHROW);
        } else {
            mw.visitInsn(ACONST_NULL);
            mw.visitInsn(ARETURN);
        }

        Label end = new Label();
        mw.visitLabel(end);
        mw.visitLocalVariable("this", getDesc(clazz), null, start, end, 0);
        mw.visitLocalVariable("methodName", "Ljava/lang/String;", null, start, end, 1);
        mw.visitLocalVariable("params", "[Ljava/lang/Object;", null, start, end, 2);
    }

    public static InvokerFactory getInstance() {
        return instance;
    }

    public static Invoker getInvoker(Object source) {
        return getInstance().innerGetInvoker(source.getClass(), source);
    }


}
