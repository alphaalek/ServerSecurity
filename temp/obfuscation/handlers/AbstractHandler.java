package me.alek.obfuscation.handlers;

import me.alek.model.FeatureResponse;
import me.alek.obfuscation.impl.AbstractObfFeature;
import org.objectweb.asm.tree.ClassNode;

import java.util.HashMap;
import java.util.List;

public interface AbstractHandler {

    List<AbstractObfFeature> getImplementedFeatures();

    HashMap<String, FeatureResponse> check(ClassNode classNode);
}
