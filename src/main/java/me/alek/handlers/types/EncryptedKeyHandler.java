package me.alek.handlers.types;

import me.alek.controllers.BytecodeController;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

import java.util.ArrayList;

public abstract class EncryptedKeyHandler extends InsnInvokeHandler {

    public EncryptedKeyHandler() {
        super(MethodInsnNode.class);
    }

    @Override
    public String processAbstractInsn(AbstractInsnNode abstractInsnNode) {
        MethodInsnNode methodInsnNode = (MethodInsnNode) abstractInsnNode;
        ArrayList<String> testStrings = new ArrayList<>();
        switch (methodInsnNode.owner) {
            case "java/net/URI":

                //WEBSOCKET URI CHECK
                if (methodInsnNode.name.equals("create")) {
                    String websocketURI =  BytecodeController.getStringUsed(methodInsnNode);
                    if (websocketURI != null) {
                        testStrings.add(websocketURI);
                    }
                }
            case "java/lang/String": {

                if (methodInsnNode.getOpcode() == Opcodes.INVOKESPECIAL) {

                    String bytesInvocation = BytecodeController.getBytesInvocation(methodInsnNode);
                    if (bytesInvocation != null) {
                        testStrings.add(bytesInvocation);
                    }
                }
            }
            case "java/util/Base64$Decoder": {

               // System.out.println(methodInsnNode.name);

                String base64Invocation = BytecodeController.getBase64Invocation(methodInsnNode);
                if (base64Invocation != null) {
                    testStrings.add(base64Invocation);
                }
            }
        }

        for (String testString : testStrings) {
            for (String url : getURLKeys()) {

                if (testString.equals(url)) {
                    if (url.contains("hostflow")) {
                        return "Websocket";
                    }
                    return "Key";
                }
            }
        }
        return null;
    }

    public abstract String[] getURLKeys();
}
