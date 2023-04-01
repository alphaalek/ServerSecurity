package me.alek.obfuscation.impl.fields;

import me.alek.model.AttributeStatus;
import me.alek.enums.Risk;
import me.alek.obfuscation.impl.AbstractFieldObfFeature;
import me.alek.obfuscation.impl.AbstractObfFeature;
import org.objectweb.asm.tree.FieldNode;

import java.util.regex.Pattern;

public class StringVowelFeature extends AbstractObfFeature implements AbstractFieldObfFeature {

    private final Pattern regexPattern;

    public StringVowelFeature() {
        this.regexPattern = Pattern.compile("[aeiouy]");
    }

    @Override
    public String getName() {
        return "String Vowel Count";
    }

    @Override
    public void affectAttributeStatus(AttributeStatus attributeStatusModel, FieldNode field) {
        Object valueObject = field.value;
        if (valueObject instanceof String) {
            String stringObject = (String) valueObject;

            for (String str : stringObject.split("")) {
                affectAttributeStatusGlobally(attributeStatusModel, regexPattern.matcher(str).find());
            }
        }
    }

    @Override
    public Risk getFeatureRisk() {
        return Risk.LOW;
    }


    @Override
    public boolean feedback(AttributeStatus attributeStatusModel) {
        return attributeStatusModel.generatePercentage() < 0.2;
    }

}
