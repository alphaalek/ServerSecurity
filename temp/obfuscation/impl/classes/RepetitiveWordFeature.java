package me.alek.obfuscation.impl.classes;

import me.alek.model.AttributeStatus;
import me.alek.utils.Utils;
import me.alek.enums.Risk;
import me.alek.obfuscation.impl.AbstractClassObfFeature;
import me.alek.obfuscation.impl.AbstractObfFeature;
import org.objectweb.asm.tree.ClassNode;

public class RepetitiveWordFeature extends AbstractObfFeature implements AbstractClassObfFeature {
    @Override
    public void affectAttributeStatus(AttributeStatus attributeStatusModel, ClassNode classNode) {
        int freq = Utils.frequencyOfWord(classNode);

        attributeStatusModel.setAbnormalCount(freq);
    }

    @Override
    public String getName() {
        return "Repetitive Word";
    }

    @Override
    public Risk getFeatureRisk() {
        return Risk.HIGH;
    }

    @Override
    public boolean feedback(AttributeStatus attributeStatusModel) {
        return attributeStatusModel.getAbnormalCount() > 10;
    }
}
