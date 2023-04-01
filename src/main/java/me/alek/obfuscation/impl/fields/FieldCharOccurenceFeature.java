package me.alek.obfuscation.impl.fields;

import me.alek.model.AttributeStatus;
import me.alek.utils.Utils;
import me.alek.enums.Risk;
import me.alek.obfuscation.impl.AbstractFieldObfFeature;
import me.alek.obfuscation.impl.AbstractObfFeature;
import org.objectweb.asm.tree.FieldNode;

public class FieldCharOccurenceFeature extends AbstractObfFeature implements AbstractFieldObfFeature {
    @Override
    public void affectAttributeStatus(AttributeStatus attributeStatusModel, FieldNode field) {
        String fieldName = field.name;
        int frequentChar = Utils.mostOccuringChar(fieldName);

        double ratio = Utils.arithmeticSecure(frequentChar, fieldName.length());
        affectAttributeStatusGlobally(attributeStatusModel, (fieldName.length() > 4 && ratio > 0.55));
    }

    @Override
    public Risk getFeatureRisk() {
        return Risk.HIGH;
    }

    @Override
    public String getName() {
        return "Field Char Occurence";
    }

    @Override
    public boolean feedback(AttributeStatus attributeStatusModel) {
        return attributeStatusModel.getAbnormalCount() > 0;
    }
}
