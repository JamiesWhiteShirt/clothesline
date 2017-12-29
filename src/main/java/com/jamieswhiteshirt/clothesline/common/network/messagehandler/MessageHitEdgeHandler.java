package com.jamieswhiteshirt.clothesline.common.network.messagehandler;

import com.jamieswhiteshirt.clothesline.api.INetworkManager;
import com.jamieswhiteshirt.clothesline.common.Util;
import com.jamieswhiteshirt.clothesline.common.network.message.MessageHitEdge;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nullable;

public class MessageHitEdgeHandler implements IMessageHandler<MessageHitEdge, IMessage> {
    @CapabilityInject(INetworkManager.class)
    private static final Capability<INetworkManager> NETWORK_MANAGER_CAPABILITY = Util.nonNullInjected();

    @Nullable
    @Override
    public IMessage onMessage(MessageHitEdge message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().player;
        WorldServer world = player.getServerWorld();
        world.addScheduledTask(() -> {
            INetworkManager manager = world.getCapability(NETWORK_MANAGER_CAPABILITY, null);
            if (manager != null) {
                //TODO: Validate this edge position
                manager.disconnect(message.fromPos, message.toPos);
            }
        });
        return null;
    }
}
