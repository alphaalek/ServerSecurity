package me.alek.obfuscation.impl.methods;

import me.alek.model.AttributeStatus;
import me.alek.enums.Risk;
import me.alek.obfuscation.impl.AbstractMethodObfFeature;
import me.alek.obfuscation.impl.AbstractObfFeature;
import org.objectweb.asm.tree.MethodNode;

public class AllatoriMethodFeature extends AbstractObfFeature implements AbstractMethodObfFeature {
    @Override
    public String getName() {
        return "Allatori Method";
    }

    @Override
    public Risk getFeatureRisk() {
        return Risk.CRITICAL;
    }

    @Override
    public boolean feedback(AttributeStatus attributeStatusModel) {
        return attributeStatusModel.getAbnormalCount() > 0;
    }

    @Override
    public void affectAttributeStatus(AttributeStatus attributeStatusModel, MethodNode method) {
        String methodName = method.name;

        affectAttributeStatusGlobally(attributeStatusModel, (methodName.toUpperCase().contains("ALLATORI")));
    }
}
