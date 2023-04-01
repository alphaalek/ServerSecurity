package me.alek.model;

import lombok.Getter;
import lombok.Setter;
import me.alek.utils.Utils;

public class AttributeStatus {

    @Getter
    @Setter
    private double totalCount;

    @Getter
    @Setter
    private double abnormalCount;


    public AttributeStatus() {
        totalCount = 0;
        abnormalCount = 0;
    }

    public void incrementAbnormal() {
        abnormalCount++;
        totalCount++;
    }

    public void incrementTotal() {
        totalCount++;
    }

    public Double generatePercentage() {
        return Utils.arithmeticSecure(abnormalCount, totalCount);
    }

}
