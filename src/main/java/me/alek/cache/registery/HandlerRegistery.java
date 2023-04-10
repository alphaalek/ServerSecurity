package me.alek.cache.registery;

import me.alek.cache.Container;
import me.alek.cache.Registery;
import me.alek.handlers.BaseHandler;
import me.alek.handlers.impl.*;
import me.alek.handlers.impl.detections.*;
import me.alek.handlers.types.ObfuscationHandler;

import java.util.Arrays;
import java.util.List;

public class HandlerRegistery extends Registery<BaseHandler> {

    public HandlerRegistery(Container<BaseHandler> container) {
        super(container);
    }

    @Override
    public List<BaseHandler> getElements() {
        return Arrays.asList(
                //DETECTIONS
                new Base64Check(),
                //new OpenConnectionCheck(),
                new SystemAccessCheck(),
                new SystemPropertyCheck(),
                new ObfuscationHandler(),
                new L10ClassCheck(),
                new WebsocketCheck(),
                new DispatchCommandCheck(),
                new ForceOPCheck(),
                new UserAgentRequestCheck(),
                new ApplicationJsonCheck(),
                new LoadPluginCheck(),
                new CipherEncryptionCheck(),
                new HiddenFileCheck(),
                new BytecodeManipulationCheck(),
                new IPGrapperCheck(),
                new ClassLoaderCheck(),
                new EmbeddedJarCheck(),
                new VulcanCheck(),
                new DiscordWebhookCheck(),
                new CancelledChatEventCheck(),

                //MALWARES
                new HostflowCheck(),
                new SkyRageCheck(),
                new EctasyCheck(),
                new OpenEctasyCheck(),
                new QlutchCheck(),
                new ThiccIndustriesCheck(),
                new OpenBukloitCheck(),
                new BukloitCheck()
        );
    }
}
