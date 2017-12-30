package com.jamieswhiteshirt.clothesline.common;

import com.jamieswhiteshirt.clothesline.Clothesline;
import com.jamieswhiteshirt.clothesline.common.impl.NetworkManager;
import com.jamieswhiteshirt.clothesline.common.impl.SynchronizationListener;
import com.jamieswhiteshirt.clothesline.common.network.message.MessageStopUsingItemOn;
import com.jamieswhiteshirt.clothesline.common.network.message.MessageHitAttachment;
import com.jamieswhiteshirt.clothesline.common.network.message.MessageHitNetwork;
import com.jamieswhiteshirt.clothesline.common.network.message.MessageTryUseItemOnNetwork;
import com.jamieswhiteshirt.clothesline.common.network.messagehandler.MessageStopUsingItemOnHandler;
import com.jamieswhiteshirt.clothesline.common.network.messagehandler.MessageHitAttachmentHandler;
import com.jamieswhiteshirt.clothesline.common.network.messagehandler.MessageHitNetworkHandler;
import com.jamieswhiteshirt.clothesline.common.network.messagehandler.MessageTryUseItemOnNetworkHandler;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

public abstract class CommonProxy {
    public void preInit(FMLPreInitializationEvent event) {
    }

    public void init(FMLInitializationEvent event) {
        Clothesline.instance.networkWrapper.registerMessage(new MessageHitNetworkHandler(), MessageHitNetwork.class, 6, Side.SERVER);
        Clothesline.instance.networkWrapper.registerMessage(new MessageTryUseItemOnNetworkHandler(), MessageTryUseItemOnNetwork.class, 7, Side.SERVER);
        Clothesline.instance.networkWrapper.registerMessage(new MessageHitAttachmentHandler(), MessageHitAttachment.class, 8, Side.SERVER);
        Clothesline.instance.networkWrapper.registerMessage(new MessageStopUsingItemOnHandler(), MessageStopUsingItemOn.class, 9, Side.SERVER);
    }

    public NetworkManager createNetworkManager(World world) {
        NetworkManager manager = new NetworkManager(world);
        if (world instanceof WorldServer) {
            manager.addEventListener(new SynchronizationListener((WorldServer) world, Clothesline.instance.networkWrapper));
        }
        return manager;
    }
}
