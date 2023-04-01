package me.alek.obfuscation.impl;

import me.alek.handlers.types.ObfuscationHandler;
import me.alek.model.AttributeStatus;
import me.alek.enums.Risk;
import me.alek.utils.ZipUtils;

public abstract class AbstractLengthBasedObfFeature extends AbstractObfFeature {



    public String fixClass(String classPath) {
        return ZipUtils.getRootClass(classPath);
    }

    @Override
    public boolean feedback(AttributeStatus attributeStatusModel) {
        return attributeStatusModel.generatePercentage() > 0.6;
    }

    @Override
    public Risk getFeatureRisk() {
        return Risk.LOW;
    }

    public void affectAttributeStatus(AttributeStatus attributeStatusModel, String string) {
        boolean shouldFlag = false;
        if (string.length() <= 3) {
            boolean isAcceptedName = false;
            for (String nameCheck : ObfuscationHandler.getAcceptedNameObfContainer().getList()) {
                if (string.equalsIgnoreCase(nameCheck)) {
                    isAcceptedName = true;
                    break;
                }
            }
            shouldFlag = !isAcceptedName;
        }
        affectAttributeStatusGlobally(attributeStatusModel, shouldFlag);
    }
}
