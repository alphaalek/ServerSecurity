package me.alek.obfuscation.impl.fields;

import me.alek.model.AttributeStatus;
import me.alek.enums.Risk;
import me.alek.obfuscation.impl.AbstractFieldObfFeature;
import me.alek.obfuscation.impl.AbstractObfFeature;
import org.objectweb.asm.tree.FieldNode;

public class FieldByteArrayFeature extends AbstractObfFeature implements AbstractFieldObfFeature {
    @Override
    public void affectAttributeStatus(AttributeStatus attributeStatusModel, FieldNode field) {
        String desc = field.desc;

        affectAttributeStatusGlobally(attributeStatusModel, (desc.equals("[B")));
    }

    @Override
    public String getName() {
        return "Field Byte Array";
    }

    @Override
    public Risk getFeatureRisk() {
        return Risk.LOW;
    }

    @Override
    public boolean feedback(AttributeStatus attributeStatusModel) {
        return attributeStatusModel.getAbnormalCount() > 0;
    }
}
