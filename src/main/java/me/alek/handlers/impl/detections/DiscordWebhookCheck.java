package me.alek.handlers.impl.detections;

import me.alek.controllers.BytecodeController;
import me.alek.enums.Risk;
import me.alek.handlers.types.MethodInvokeHandler;
import me.alek.handlers.types.RequestPropertyHandler;
import me.alek.handlers.types.nodes.DetectionNode;
import me.alek.model.PluginProperties;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;

public class DiscordWebhookCheck extends RequestPropertyHandler {

    public DiscordWebhookCheck() {
        super();
    }

    @Override
    public String getType() {
        return "Discord Webhook";
    }

    @Override
    public Risk getRisk() {
        return Risk.LOW;

    }

    @Override
    public String[] getParams() {
        return new String[]{"application/json", "Content-Type"};
    }
}
