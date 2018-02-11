package com.jamieswhiteshirt.clothesline.server;

import com.jamieswhiteshirt.clothesline.common.CommonProxy;
import com.jamieswhiteshirt.clothesline.common.network.message.*;
import com.jamieswhiteshirt.clothesline.common.network.messagehandler.DummyMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.SERVER)
public class ServerProxy extends CommonProxy {
    @Override
    public SimpleNetworkWrapper createNetworkChannel() {
        SimpleNetworkWrapper networkChannel = super.createNetworkChannel();
        networkChannel.registerMessage(DummyMessageHandler.INSTANCE, SetNetworkMessage.class, 0, Side.CLIENT);
        networkChannel.registerMessage(DummyMessageHandler.INSTANCE, AddNetworkMessage.class, 1, Side.CLIENT);
        networkChannel.registerMessage(DummyMessageHandler.INSTANCE, RemoveNetworkMessage.class, 2, Side.CLIENT);
        networkChannel.registerMessage(DummyMessageHandler.INSTANCE, SetAttachmentMessage.class, 3, Side.CLIENT);
        networkChannel.registerMessage(DummyMessageHandler.INSTANCE, RemoveAttachmentMessage.class, 4, Side.CLIENT);
        networkChannel.registerMessage(DummyMessageHandler.INSTANCE, SetNetworkStateMessage.class, 5, Side.CLIENT);
        networkChannel.registerMessage(DummyMessageHandler.INSTANCE, SetConnectorPosMessage.class, 10, Side.CLIENT);
        networkChannel.registerMessage(DummyMessageHandler.INSTANCE, UpdateNetworkMessage.class, 11, Side.CLIENT);
        return networkChannel;
    }
}
