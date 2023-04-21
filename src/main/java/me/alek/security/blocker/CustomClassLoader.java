package me.alek.security.blocker;

import java.security.ProtectionDomain;
import java.security.SecureClassLoader;

public class CustomClassLoader extends SecureClassLoader {
    private final String className;
    private final byte[] classBytes;

    public CustomClassLoader(String className, byte[] classBytes) {
        this.className = className;
        this.classBytes = classBytes;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        if (name.equals(className)) {
            return defineClass(name, classBytes, 0, classBytes.length);
        }
        return super.findClass(name);
    }
}
