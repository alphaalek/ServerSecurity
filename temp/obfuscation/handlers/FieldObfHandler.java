package me.alek.obfuscation.handlers;

import me.alek.model.AttributeStatus;
import me.alek.obfuscation.impl.AbstractFieldObfFeature;
import me.alek.obfuscation.impl.AbstractObfFeature;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FieldObfHandler extends BaseAbstractHandler {

    private final ArrayList<AbstractObfFeature> implementedAbstractFieldFeatures = new ArrayList<>();

    public FieldObfHandler(AbstractObfFeature... implementedAbstractFieldFeatures) {
        this.implementedAbstractFieldFeatures.addAll(Arrays.asList(implementedAbstractFieldFeatures));
    }

    @Override
    public List<AbstractObfFeature> getImplementedFeatures() {
        return implementedAbstractFieldFeatures;
    }

    @Override
    public void affectAttributes(ClassNode classNode, AbstractObfFeature abstractObfFeature, AttributeStatus attributeStatusModel) {
        AbstractFieldObfFeature feature = (AbstractFieldObfFeature) abstractObfFeature;;
        for (FieldNode field : classNode.fields) {
            feature.affectAttributeStatus(attributeStatusModel, field);
        }
    }
}
