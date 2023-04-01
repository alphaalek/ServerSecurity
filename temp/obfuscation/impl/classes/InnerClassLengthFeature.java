package me.alek.obfuscation.impl.classes;

import me.alek.model.AttributeStatus;
import me.alek.enums.Risk;
import me.alek.obfuscation.impl.AbstractClassObfFeature;
import me.alek.obfuscation.impl.AbstractLengthBasedObfFeature;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InnerClassNode;

public class InnerClassLengthFeature extends AbstractLengthBasedObfFeature implements AbstractClassObfFeature {
    @Override
    public void affectAttributeStatus(AttributeStatus attributeStatusModel, ClassNode classNode) {
        for (InnerClassNode innerClassPath : classNode.innerClasses) {
            affectAttributeStatus(attributeStatusModel, fixClass(innerClassPath.name));
        }
    }

    @Override
    public Risk getFeatureRisk() {
        return Risk.HIGH;
    }

    @Override
    public String getName() {
        return "Super Class Name Length";
    }

}
