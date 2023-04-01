package me.alek.model;

import lombok.Getter;
import me.alek.enums.Risk;

public class FeatureResponse {


    @Getter
    private final Boolean feedback;

    @Getter
    private final AttributeStatus attributeStatus;

    @Getter
    private final Risk featureRisk;

    private boolean relevant = true;

    public FeatureResponse(boolean feedback, AttributeStatus attributeStatus, Risk featureRisk) {
        this.feedback = feedback;
        this.attributeStatus = attributeStatus;
        this.featureRisk = featureRisk;
        if (attributeStatus != null) {
            if (attributeStatus.getTotalCount() == 0) {
                relevant = false;
            }
        }
    }

    public boolean isRelevant() {
        return relevant;
    }
}
