package me.alek.serversecurity.security.blocker;

import org.bukkit.event.Event;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.HashMap;

public class AnnotationInjectedVisitor<EVENT extends Event> extends ClassVisitor {

    private final ClassNode classNode;
    private final Class<EVENT> clazz;
    private static String check;
    private final HashMap<String, MethodNode> annotatedMethodNodeMap = new HashMap<>();

    public ClassNode getClassNode() {
        return classNode;
    }

    public AnnotationInjectedVisitor(Class<EVENT> clazz, ClassNode classNode, String check) {
        super(Opcodes.ASM7, classNode);
        this.clazz = clazz;
        this.classNode = classNode;
        this.check = check;
    }

    private String getMethodSignature(MethodNode methodNode) {
        return methodNode.name + methodNode.desc;
    }

    private interface SpecificAnnotationListener {

        boolean isSatisfiedAnnotation();

        void onSatisfiedAnnotation();

        String getMethod();
    }

    public static class MethodScannerWrapper extends MethodVisitor {

        private SpecificAnnotationListener listener;
        private final MethodNode methodNode;

        public MethodScannerWrapper(MethodNode methodNode) {
            super(Opcodes.ASM7, methodNode);
            this.methodNode = methodNode;
        }

        @Override
        public void visitInsn(int opcode) {
            super.visitInsn(opcode);
            methodNode.visitInsn(opcode);
        }

        @Override
        public void visitEnd() {
            super.visitEnd();
            methodNode.visitEnd();
        }

        public void addListener(SpecificAnnotationListener listener) {
            this.listener = listener;
        }

        @Override
        public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            if (desc.contains(check)) {
                if (listener.isSatisfiedAnnotation()) {
                    listener.onSatisfiedAnnotation();
                }
            }
            return super.visitAnnotation(desc, visible);
        }
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodNode methodNode = new MethodNode(Opcodes.ASM7, access, name, desc, signature, exceptions);
        MethodScannerWrapper wrapper = new MethodScannerWrapper(methodNode);

        wrapper.addListener(new SpecificAnnotationListener() {

            @Override
            public boolean isSatisfiedAnnotation() {
                return desc.toLowerCase().contains(getMethod());
            }

            @Override
            public void onSatisfiedAnnotation() {
                AnnotationInjectedVisitor.super.visitMethod(access, name, desc, signature, exceptions);
                annotatedMethodNodeMap.put(getMethodSignature(methodNode), methodNode);
            }
            @Override
            public String getMethod() {
                return clazz.getSimpleName().toLowerCase();
            }
        });
        return wrapper;
    }

    public HashMap<String, MethodNode> getAnnotatedMethodNodeMap() {
        return annotatedMethodNodeMap;
    }
}
