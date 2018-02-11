package com.jamieswhiteshirt.clothesline.server;

import com.jamieswhiteshirt.clothesline.Clothesline;
import com.jamieswhiteshirt.clothesline.common.CommonProxy;
import com.jamieswhiteshirt.clothesline.common.network.message.*;
import com.jamieswhiteshirt.clothesline.common.network.messagehandler.DummyMessageHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.SERVER)
public class ServerProxy extends CommonProxy {
    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        Clothesline.instance.networkWrapper.registerMessage(new DummyMessageHandler(), SetNetworkMessage.class, 0, Side.CLIENT);
        Clothesline.instance.networkWrapper.registerMessage(new DummyMessageHandler(), AddNetworkMessage.class, 1, Side.CLIENT);
        Clothesline.instance.networkWrapper.registerMessage(new DummyMessageHandler(), RemoveNetworkMessage.class, 2, Side.CLIENT);
        Clothesline.instance.networkWrapper.registerMessage(new DummyMessageHandler(), SetAttachmentMessage.class, 3, Side.CLIENT);
        Clothesline.instance.networkWrapper.registerMessage(new DummyMessageHandler(), RemoveAttachmentMessage.class, 4, Side.CLIENT);
        Clothesline.instance.networkWrapper.registerMessage(new DummyMessageHandler(), SetNetworkStateMessage.class, 5, Side.CLIENT);
        Clothesline.instance.networkWrapper.registerMessage(new DummyMessageHandler(), SetConnectorPosMessage.class, 10, Side.CLIENT);
    }
}
