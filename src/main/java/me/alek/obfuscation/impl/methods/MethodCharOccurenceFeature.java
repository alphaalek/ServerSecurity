package me.alek.obfuscation.impl.methods;

import me.alek.model.AttributeStatus;
import me.alek.utils.Utils;
import me.alek.enums.Risk;
import me.alek.obfuscation.impl.AbstractMethodObfFeature;
import me.alek.obfuscation.impl.AbstractObfFeature;
import org.objectweb.asm.tree.MethodNode;

public class MethodCharOccurenceFeature extends AbstractObfFeature implements AbstractMethodObfFeature {
    @Override
    public void affectAttributeStatus(AttributeStatus attributeStatusModel, MethodNode method) {
        String methodName = method.name;
        int frequentChar = Utils.mostOccuringChar(methodName);

        double ratio = Utils.arithmeticSecure(frequentChar, methodName.length());
        affectAttributeStatusGlobally(attributeStatusModel, (methodName.length() > 6 && ratio > 0.55));
    }

    @Override
    public String getName() {
        return "Method Char Occurence";
    }

    @Override
    public Risk getFeatureRisk() {
        return Risk.HIGH;
    }


    @Override
    public boolean feedback(AttributeStatus attributeStatusModel) {
        return attributeStatusModel.getAbnormalCount() > 0;
    }
}
