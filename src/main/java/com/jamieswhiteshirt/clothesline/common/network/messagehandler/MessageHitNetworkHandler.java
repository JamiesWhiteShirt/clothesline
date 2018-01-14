package com.jamieswhiteshirt.clothesline.common.network.messagehandler;

import com.jamieswhiteshirt.clothesline.api.AbsoluteNetworkState;
import com.jamieswhiteshirt.clothesline.api.IServerNetworkManager;
import com.jamieswhiteshirt.clothesline.api.Network;
import com.jamieswhiteshirt.clothesline.api.NetworkGraph;
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
    @CapabilityInject(IServerNetworkManager.class)
    private static final Capability<IServerNetworkManager> NETWORK_MANAGER_CAPABILITY = Util.nonNullInjected();

    @Nullable
    @Override
    public IMessage onMessage(MessageHitNetwork message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().player;
        WorldServer world = player.getServerWorld();
        world.addScheduledTask(() -> {
            IServerNetworkManager manager = world.getCapability(NETWORK_MANAGER_CAPABILITY, null);
            if (manager != null) {
                Network network = manager.getNetworkById(message.networkId);
                if (network != null) {
                    AbsoluteNetworkState state = network.getState();
                    NetworkGraph.Edge edge = state.getTree().getGraphEdgeForOffset(message.offset);
                    if (Validation.canReachPos(player, edge.getPositionForOffset(message.offset))) {
                        manager.disconnect(edge.getFromKey(), edge.getToKey());
                    }
                }
            }
        });
        return null;
    }
}
