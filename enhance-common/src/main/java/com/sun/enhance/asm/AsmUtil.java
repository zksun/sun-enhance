package com.sun.enhance.asm;

import org.objectweb.asm.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zksun on 5/11/16.
 */
public final class AsmUtil {

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


    private static class MezhodAdapter extends ClassAdapter {

        private Mezhod[] mezhods;

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
            List<Mezhod> mezhodList = new ArrayList<Mezhod>();

            return super.visitMethod(access, name, desc, signature, exceptions);
        }
    }
}
