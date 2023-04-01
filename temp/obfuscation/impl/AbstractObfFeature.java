package me.alek.obfuscation.impl;

import me.alek.model.AttributeStatus;
import me.alek.enums.Risk;

public abstract class AbstractObfFeature {

    public void affectAttributeStatusGlobally(AttributeStatus attributeStatusModel, boolean check) {
        if (check) {
            attributeStatusModel.incrementAbnormal();
        } else {
            attributeStatusModel.incrementTotal();
        }
    }

    public abstract String getName();

    public abstract Risk getFeatureRisk();

    public abstract boolean feedback(AttributeStatus attributeStatusModel);
}
