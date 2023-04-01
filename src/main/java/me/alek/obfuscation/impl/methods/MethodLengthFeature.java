package me.alek.obfuscation.impl.methods;

import me.alek.model.AttributeStatus;
import me.alek.obfuscation.impl.AbstractLengthBasedObfFeature;
import me.alek.obfuscation.impl.AbstractMethodObfFeature;
import org.objectweb.asm.tree.MethodNode;

public class MethodLengthFeature extends AbstractLengthBasedObfFeature implements AbstractMethodObfFeature {

    @Override
    public String getName() {
        return "Method Name Length";
    }


    @Override
    public void affectAttributeStatus(AttributeStatus attributeStatusModel, MethodNode method) {
        affectAttributeStatus(attributeStatusModel, method.name);
    }
}
