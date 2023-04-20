package me.alek.security.blocker;

import org.objectweb.asm.*;

public class ServerClassVisitor extends ClassVisitor {

    public ServerClassVisitor(ClassWriter classWriter) {
        super(Opcodes.ASM9, classWriter);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        if (name.equals("commandMap")) {
            access = Opcodes.ACC_PUBLIC; //| Opcodes.ACC_STATIC;

            desc = "Lme/alek/security/blocker/wrappers/WrappedCommandMap;";
        }
        return super.visitField(access, name, desc, signature, value);
    }

    /*@Override
    public void visitEnd() {
        addSetCommandMapMethod();
        super.visitEnd();
    }

    private void addSetCommandMapMethod() {
        MethodVisitor mv = super.visitMethod(Opcodes.ACC_PUBLIC, "setCommandMap", "(Lorg/bukkit/command/CommandMap;)V", null, null);
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitVarInsn(Opcodes.ALOAD, 1);
        mv.visitFieldInsn(Opcodes.PUTFIELD, "org/bukkit/craftbukkit/v1_8_R3/CraftServer", "commandMap", "Lorg/bukkit/command/CommandMap;");
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(2, 2);
        mv.visitEnd();
    }*/

    /*@Override
    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        if (name.equals("org/bukkit/craftbukkit/v1_8_R3/CraftServer$BooleanWrapper")) {
            access = Opcodes.ACC_PUBLIC; // Add this line
        }
        super.visitInnerClass(name, outerName, innerName, access);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
        if (name.equals("getServerIcon") || name.equals("setUseHelp")) {
            return new MethodVisitor(Opcodes.ASM5, mv) {
                @Override
                public void visitFieldInsn(int opcode, String owner, String name, String desc) {
                    if (owner.equals("org/bukkit/craftbukkit/v1_8_R3/CraftServer$BooleanWrapper")) {
                        owner = "me.alek.security.BooleanWrapper";
                    }
                    super.visitFieldInsn(opcode, owner, name, desc);
                }
            };
        }
        return mv;
    }*/

}



