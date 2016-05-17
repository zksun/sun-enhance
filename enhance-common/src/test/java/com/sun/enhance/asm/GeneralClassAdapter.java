package com.sun.enhance.asm;

import org.objectweb.asm.*;

import static org.objectweb.asm.Opcodes.*;

/**
 * Created by zksun on 5/17/16.
 */
class GeneralClassAdapter extends ClassAdapter {

    /**
     * Constructs a new {@link ClassAdapter} object.
     *
     * @param cv the class visitor to which this adapter must delegate calls.
     */
    GeneralClassAdapter(ClassVisitor cv) {
        super(cv);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions);
        if (name.equals("sayHello")) {
            return new SayHelloMethod(methodVisitor);
        } else {
            return methodVisitor;
        }
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        return super.visitField(access, name, desc, signature, value);
    }

    class SayHelloMethod extends MethodAdapter {
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


