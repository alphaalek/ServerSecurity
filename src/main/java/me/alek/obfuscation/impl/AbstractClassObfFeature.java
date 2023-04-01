package me.alek.obfuscation.impl;

import me.alek.model.AttributeStatus;
import org.objectweb.asm.tree.ClassNode;

public interface AbstractClassObfFeature extends AbstractBaseObfFeature {

    void affectAttributeStatus(AttributeStatus attributeStatusModel, ClassNode classNode);
}
