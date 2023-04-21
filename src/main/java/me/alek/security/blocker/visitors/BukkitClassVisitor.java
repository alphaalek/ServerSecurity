package me.alek.security.blocker.visitors;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class BukkitClassVisitor extends ClassVisitor {

    public BukkitClassVisitor(int api, ClassVisitor classVisitor) {
        super(api, classVisitor);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        if ("setServer".equals(name)) {
            return new RemoveCheckMethodVisitor(api, mv);
        }
        return mv;
    }

    private static class RemoveCheckMethodVisitor extends MethodVisitor {

        private boolean skipNextInsn;

        public RemoveCheckMethodVisitor(int api, MethodVisitor methodVisitor) {
            super(api, methodVisitor);
        }

        @Override
        public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
            if (opcode == Opcodes.GETSTATIC && "server".equals(name) && "Lorg/bukkit/Server;".equals(descriptor)) {
                skipNextInsn = true;
                Label skipLabel = new Label();
                mv.visitJumpInsn(Opcodes.GOTO, skipLabel);
                super.visitFieldInsn(opcode, owner, name, descriptor);
                mv.visitLabel(skipLabel);
            } else {
                super.visitFieldInsn(opcode, owner, name, descriptor);
            }
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
            if (skipNextInsn && opcode == Opcodes.INVOKEVIRTUAL && "java/lang/UnsupportedOperationException".equals(owner) && "<init>".equals(name)) {
                skipNextInsn = false;
            } else {
                super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
            }
        }

        @Override
        public void visitInsn(int opcode) {
            if (skipNextInsn && opcode == Opcodes.ATHROW) {
                skipNextInsn = false;
            } else {
                super.visitInsn(opcode);
            }
        }
    }
}
