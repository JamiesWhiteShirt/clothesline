package com.jamieswhiteshirt.clothesline.common;

import com.jamieswhiteshirt.clothesline.Clothesline;
import com.jamieswhiteshirt.clothesline.common.network.message.StopUsingItemOnMessage;
import com.jamieswhiteshirt.clothesline.common.network.message.HitAttachmentMessage;
import com.jamieswhiteshirt.clothesline.common.network.message.HitNetworkMessage;
import com.jamieswhiteshirt.clothesline.common.network.message.TryUseItemOnNetworkMessage;
import com.jamieswhiteshirt.clothesline.common.network.messagehandler.StopUsingItemOnMessageHandler;
import com.jamieswhiteshirt.clothesline.common.network.messagehandler.HitAttachmentMessageHandler;
import com.jamieswhiteshirt.clothesline.common.network.messagehandler.HitNetworkMessageHandler;
import com.jamieswhiteshirt.clothesline.common.network.messagehandler.TryUseItemOnNetworkMessageHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public abstract class CommonProxy {
    public SimpleNetworkWrapper createNetworkChannel() {
        SimpleNetworkWrapper networkChannel = NetworkRegistry.INSTANCE.newSimpleChannel(Clothesline.MODID);
        networkChannel.registerMessage(new HitNetworkMessageHandler(), HitNetworkMessage.class, 6, Side.SERVER);
        networkChannel.registerMessage(new TryUseItemOnNetworkMessageHandler(), TryUseItemOnNetworkMessage.class, 7, Side.SERVER);
        networkChannel.registerMessage(new HitAttachmentMessageHandler(), HitAttachmentMessage.class, 8, Side.SERVER);
        networkChannel.registerMessage(new StopUsingItemOnMessageHandler(), StopUsingItemOnMessage.class, 9, Side.SERVER);
        return networkChannel;
    }

    public void preInit(FMLPreInitializationEvent event) { }

    public void init(FMLInitializationEvent event) { }
}
