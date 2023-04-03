package me.alek.controllers;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;

public class BytecodeController {

    private static byte[] byteArrayWrapper(ArrayList<Byte> bytes) {
        byte[] primitiveBytes = new byte[bytes.size()];
        int i = 0;
        for (Byte b : reversedBytes(bytes)) {
            primitiveBytes[i] = b;
            i++;
        }
        return primitiveBytes;
    }

    private static ArrayList<Byte> reversedBytes(ArrayList<Byte> bytes) {
        Collections.reverse(bytes);
        return bytes;
    }

    public static AbstractMap.SimpleEntry<AbstractInsnNode, String> getBytesInvocationEntry(AbstractInsnNode abstractInsnNode) {
        AbstractInsnNode previousInstruction = abstractInsnNode;
        int i = 0;
        boolean j = true;
        ArrayList<Byte> bytes = new ArrayList<>();

        while ((previousInstruction = previousInstruction.getPrevious()) != null) {
            if (previousInstruction.getOpcode() == Opcodes.BIPUSH) {
                if (j) {
                    j = false;
                    i = 0;
                    bytes.add((byte) ((IntInsnNode) previousInstruction).operand);
                }
            }
            if (previousInstruction.getOpcode() == Opcodes.DUP) {
                j = true;
            }
            if (previousInstruction.getOpcode() == Opcodes.NEWARRAY) {
                break;
            }
            if (i > 5) {
                break;
            }
            i++;
        }
        return (!bytes.isEmpty()) ? new AbstractMap.SimpleEntry<>(previousInstruction, new String(byteArrayWrapper(bytes))) : null;
    }

    public static String getBytesInvocation(AbstractInsnNode abstractInsnNode) {
        AbstractMap.SimpleEntry<AbstractInsnNode, String> bytesEntry = getBytesInvocationEntry(abstractInsnNode);
        return (bytesEntry != null) ? bytesEntry.getValue() : null;
    }

    public static String decode(String str) {
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] decoded;
        try {
            decoded = decoder.decode(str);
        } catch (IllegalArgumentException e) {
            return "";
        }
        return new String(decoded);
    }

    public static AbstractMap.SimpleEntry<AbstractInsnNode, String> getBase64InvocationEntry(AbstractInsnNode abstractInsnNode) {
        int i = 0;
        AbstractInsnNode previousInstruction = abstractInsnNode;
        String string = null;

        while (!((previousInstruction = previousInstruction.getPrevious()) instanceof LdcInsnNode)) {
            if (previousInstruction == null) {
                break;
            }
            if (i > 5) {
                break;
            }
            i++;
        }
        if (previousInstruction instanceof LdcInsnNode) {
            LdcInsnNode ldcInsnNode = (LdcInsnNode) previousInstruction;
            Object cst = ldcInsnNode.cst;
            if (!(cst instanceof String)) {
                return null;
            }
            String str = (String) cst;
            if (!(str.length() > 10)) {
                return null;
            }

            String rawString = decode(str);
            if (rawString.contains("/")) {
                string = rawString;
            }
            String subString = decode(str.substring(2));
            if (subString.contains("/")) {
                string = subString;
            }
        } else {
            String bytes = getBytesInvocation(abstractInsnNode);
            if (bytes != null) {
                string = bytes;
            }
        }
        return (string != null) ? new AbstractMap.SimpleEntry<>(previousInstruction, string) : null;
    }

    public static String getBase64Invocation(AbstractInsnNode abstractInsnNode) {
        AbstractMap.SimpleEntry<AbstractInsnNode, String> base64Entry = getBase64InvocationEntry(abstractInsnNode);
        return (base64Entry != null) ? base64Entry.getValue() : null;
    }

    public static AbstractMap.SimpleEntry<AbstractInsnNode, String> getStringUsedEntry(AbstractInsnNode abstractInsnNode) {
        AbstractInsnNode previous = abstractInsnNode.getPrevious();
        AbstractMap.SimpleEntry<AbstractInsnNode, String> stringUsedEntry;

        if (previous instanceof LdcInsnNode) {
            LdcInsnNode ldcInsnNode = (LdcInsnNode) previous;
            if (ldcInsnNode.cst instanceof String) {
                stringUsedEntry = new AbstractMap.SimpleEntry<>(previous, (String) ldcInsnNode.cst);
                return stringUsedEntry;
            }
        }
        stringUsedEntry = getBase64InvocationEntry(abstractInsnNode);
        if (stringUsedEntry != null) {
            return stringUsedEntry;
        }
        stringUsedEntry = getBytesInvocationEntry(abstractInsnNode);
        if (stringUsedEntry != null) {
            return stringUsedEntry;
        }
        return null;
    }

    public static String getStringUsed(AbstractInsnNode abstractInsnNode) {
        AbstractMap.SimpleEntry<AbstractInsnNode, String> stringUsedEntry = getStringUsedEntry(abstractInsnNode);
        return (stringUsedEntry != null) ? stringUsedEntry.getValue() : null;
    }

    public static String[] getStringsUsed(AbstractInsnNode abstractInsnNodeStart, int loops) {
        AbstractInsnNode abstractInsnNodeCurrent = abstractInsnNodeStart;
        String[] stringsUsed = new String[loops];
        for (int i = 0; i < loops; i++) {
            AbstractMap.SimpleEntry<AbstractInsnNode, String> stringUsedEntry = getStringUsedEntry(abstractInsnNodeCurrent);
            if (stringUsedEntry == null) {
                return stringsUsed;
            }
            stringsUsed[i] = stringUsedEntry.getValue();
            abstractInsnNodeCurrent = stringUsedEntry.getKey();
        }
        return stringsUsed;
    }

}
