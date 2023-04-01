package me.alek.obfuscation.impl;

import me.alek.model.AttributeStatus;
import org.objectweb.asm.tree.FieldNode;

public interface AbstractFieldObfFeature extends AbstractBaseObfFeature {

    void affectAttributeStatus(AttributeStatus attributeStatusModel, FieldNode field);
}
