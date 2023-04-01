package me.alek.obfuscation.impl;

import me.alek.model.AttributeStatus;
import org.objectweb.asm.tree.MethodNode;

public interface AbstractMethodObfFeature extends AbstractBaseObfFeature {

    void affectAttributeStatus(AttributeStatus attributeStatusModel, MethodNode method);
}
