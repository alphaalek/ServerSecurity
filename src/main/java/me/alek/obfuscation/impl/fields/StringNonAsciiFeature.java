package me.alek.obfuscation.impl.fields;

import com.google.common.base.CharMatcher;
import me.alek.enums.Risk;
import me.alek.model.AttributeStatus;
import me.alek.obfuscation.impl.AbstractFieldObfFeature;
import me.alek.obfuscation.impl.AbstractObfFeature;
import org.objectweb.asm.tree.FieldNode;

import java.nio.charset.StandardCharsets;

public class StringNonAsciiFeature extends AbstractObfFeature implements AbstractFieldObfFeature {

    private boolean isPureAscii(String string) {
        return StandardCharsets.US_ASCII.newEncoder().canEncode(string);
    }

    @Override
    public void affectAttributeStatus(AttributeStatus attributeStatusModel, FieldNode field) {
        Object valueObject = field.value;
        if (valueObject instanceof String) {
            String stringObject = (String) valueObject;

            affectAttributeStatusGlobally(attributeStatusModel, (!(isPureAscii(stringObject)) && !stringObject.contains("§")));
        }
    }

    @Override
    public Risk getFeatureRisk() {
        return Risk.HIGH;
    }

    @Override
    public String getName() {
        return "String Non Ascii";
    }

    @Override
    public boolean feedback(AttributeStatus attributeStatusModel) {
        return attributeStatusModel.generatePercentage() > 0.05;
    }
}
