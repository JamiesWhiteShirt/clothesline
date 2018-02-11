package com.jamieswhiteshirt.clothesline.common.network.messagehandler;

import com.jamieswhiteshirt.clothesline.Clothesline;
import com.jamieswhiteshirt.clothesline.api.IServerNetworkManager;
import com.jamieswhiteshirt.clothesline.api.Network;
import com.jamieswhiteshirt.clothesline.api.Graph;
import com.jamieswhiteshirt.clothesline.common.network.message.MessageHitNetwork;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nullable;

public class MessageHitNetworkHandler implements IMessageHandler<MessageHitNetwork, IMessage> {
    @Nullable
    @Override
    public IMessage onMessage(MessageHitNetwork message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().player;
        WorldServer world = player.getServerWorld();
        world.addScheduledTask(() -> {
            IServerNetworkManager manager = world.getCapability(Clothesline.SERVER_NETWORK_MANAGER_CAPABILITY, null);
            if (manager != null) {
                Network network = manager.getNetworkById(message.networkId);
                if (network != null) {
                    Graph.Edge edge = network.getState().getGraph().getEdgeForOffset(message.offset);
                    if (Validation.canReachPos(player, edge.getPositionForOffset(message.offset))) {
                        manager.disconnect(edge.getFromKey(), edge.getToKey());
                    }
                }
            }
        });
        return null;
    }
}
