package me.alek.obfuscation.impl.fields;

import me.alek.model.AttributeStatus;
import me.alek.enums.Risk;
import me.alek.obfuscation.impl.AbstractFieldObfFeature;
import me.alek.obfuscation.impl.AbstractObfFeature;
import org.objectweb.asm.tree.FieldNode;

import java.util.regex.Pattern;

public class FieldCharSpecialFeature extends AbstractObfFeature implements AbstractFieldObfFeature {

    private final Pattern regexPattern;

    public FieldCharSpecialFeature() {
        this.regexPattern = Pattern.compile("[A-Za-z0-9_\\$]");
    }

    @Override
    public void affectAttributeStatus(AttributeStatus attributeStatusModel, FieldNode field) {
        String fieldName = field.name;

        affectAttributeStatusGlobally(attributeStatusModel, !regexPattern.matcher(fieldName).find());
    }

    @Override
    public Risk getFeatureRisk() {
        return Risk.HIGH;
    }

    @Override
    public String getName() {
        return "Field Special Char";
    }

    @Override
    public boolean feedback(AttributeStatus attributeStatusModel) {
        return attributeStatusModel.getAbnormalCount() >= 1;
    }


}
