package me.alek.obfuscation.impl.fields;

import me.alek.model.AttributeStatus;
import me.alek.enums.Risk;
import me.alek.obfuscation.impl.AbstractFieldObfFeature;
import me.alek.obfuscation.impl.AbstractObfFeature;
import org.objectweb.asm.tree.FieldNode;

public class NumberSizeFeature extends AbstractObfFeature implements AbstractFieldObfFeature {

    @Override
    public void affectAttributeStatus(AttributeStatus attributeStatusModel, FieldNode field) {
        Object valueObject = field.value;
        if (valueObject instanceof Long) {
            Long longValue = (Long) valueObject;

            affectAttributeStatusGlobally(attributeStatusModel, (longValue > 250000));
        }
    }

    @Override
    public Risk getFeatureRisk() {
        return Risk.LOW;
    }

    @Override
    public String getName() {
        return "Number Size";
    }

    @Override
    public boolean feedback(AttributeStatus attributeStatusModel) {
        return attributeStatusModel.getAbnormalCount() >= 1;
    }
}
