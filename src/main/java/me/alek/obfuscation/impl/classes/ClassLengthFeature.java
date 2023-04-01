package me.alek.obfuscation.impl.classes;

import me.alek.model.AttributeStatus;
import me.alek.enums.Risk;
import me.alek.obfuscation.impl.AbstractClassObfFeature;
import me.alek.obfuscation.impl.AbstractLengthBasedObfFeature;
import org.objectweb.asm.tree.ClassNode;

public class ClassLengthFeature extends AbstractLengthBasedObfFeature implements AbstractClassObfFeature {

    @Override
    public void affectAttributeStatus(AttributeStatus attributeStatusModel, ClassNode classNode) {
        affectAttributeStatus(attributeStatusModel, fixClass(classNode.name));
    }

    @Override
    public String getName() {
        return "Class Name Length";
    }

    @Override
    public Risk getFeatureRisk() {
        return Risk.HIGH;
    }

}
