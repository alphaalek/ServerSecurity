package me.alek.security.blocker.visitors;

import org.objectweb.asm.*;

public class ServerClassVisitor extends ClassVisitor {

    public ServerClassVisitor(ClassWriter classWriter) {
        super(Opcodes.ASM9, classWriter);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        if (name.equals("commandMap")) {
            access &= ~Opcodes.ACC_FINAL;
            descriptor = "Lorg/bukkit/command/CommandMap;";
        }
        return super.visitField(access, name, descriptor, signature, value);
    }
}



