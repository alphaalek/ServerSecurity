package me.alek.obfuscation.impl.fields;

import me.alek.model.AttributeStatus;
import me.alek.obfuscation.impl.AbstractFieldObfFeature;
import me.alek.obfuscation.impl.AbstractLengthBasedObfFeature;
import org.objectweb.asm.tree.FieldNode;

public class FieldLengthFeature extends AbstractLengthBasedObfFeature implements AbstractFieldObfFeature {

    @Override
    public String getName() {
        return "Field Name Length";
    }

    @Override
    public void affectAttributeStatus(AttributeStatus attributeStatusModel, FieldNode field) {
        affectAttributeStatus(attributeStatusModel, field.name);
    }
}
