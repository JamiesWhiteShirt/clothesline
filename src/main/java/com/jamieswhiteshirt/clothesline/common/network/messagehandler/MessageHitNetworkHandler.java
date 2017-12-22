package com.jamieswhiteshirt.clothesline.common.network.messagehandler;

import com.jamieswhiteshirt.clothesline.api.INetworkManager;
import com.jamieswhiteshirt.clothesline.common.Util;
import com.jamieswhiteshirt.clothesline.common.network.message.MessageHitNetwork;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nullable;

public class MessageHitNetworkHandler implements IMessageHandler<MessageHitNetwork, IMessage> {
    @CapabilityInject(INetworkManager.class)
    private static final Capability<INetworkManager> CLOTHESLINE_NETWORK_MANAGER_CAPABILITY = Util.nonNullInjected();

    @Nullable
    @Override
    public IMessage onMessage(MessageHitNetwork message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().player;
        WorldServer world = player.getServerWorld();
        world.addScheduledTask(() -> {
            INetworkManager manager = world.getCapability(CLOTHESLINE_NETWORK_MANAGER_CAPABILITY, null);
            if (manager != null) {
                //TODO: Validate this edge position
                manager.disconnect(message.posA, message.posB);
            }
        });
        return null;
    }
}
