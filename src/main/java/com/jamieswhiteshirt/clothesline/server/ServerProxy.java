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
        Clothesline.instance.networkWrapper.registerMessage(new DummyMessageHandler(), MessageSetNetworks.class, 0, Side.CLIENT);
        Clothesline.instance.networkWrapper.registerMessage(new DummyMessageHandler(), MessageAddNetwork.class, 1, Side.CLIENT);
        Clothesline.instance.networkWrapper.registerMessage(new DummyMessageHandler(), MessageRemoveNetwork.class, 2, Side.CLIENT);
        Clothesline.instance.networkWrapper.registerMessage(new DummyMessageHandler(), MessageSetAttachment.class, 3, Side.CLIENT);
        Clothesline.instance.networkWrapper.registerMessage(new DummyMessageHandler(), MessageRemoveAttachment.class, 4, Side.CLIENT);
        Clothesline.instance.networkWrapper.registerMessage(new DummyMessageHandler(), MessageSetNetworkState.class, 5, Side.CLIENT);
        Clothesline.instance.networkWrapper.registerMessage(new DummyMessageHandler(), MessageSetConnectorPos.class, 10, Side.CLIENT);
    }
}
