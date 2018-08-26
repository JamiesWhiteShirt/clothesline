package com.jamieswhiteshirt.clothesline.common.network.messagehandler;

import com.jamieswhiteshirt.clothesline.Clothesline;
import com.jamieswhiteshirt.clothesline.api.*;
import com.jamieswhiteshirt.clothesline.common.network.message.HitNetworkMessage;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nullable;

public class HitNetworkMessageHandler implements IMessageHandler<HitNetworkMessage, IMessage> {
    @Nullable
    @Override
    public IMessage onMessage(HitNetworkMessage message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().player;
        WorldServer world = player.getServerWorld();
        world.addScheduledTask(() -> {
            INetworkManager manager = world.getCapability(Clothesline.NETWORK_MANAGER_CAPABILITY, null);
            if (manager != null) {
                INetwork network = manager.getNetworkById(message.networkId);
                if (network != null) {
                    Path.Edge edge = network.getState().getPath().getEdgeForPosition(message.offset);
                    if (Validation.canReachPos(player, edge.getPositionForOffset(message.offset))) {
                        Line line = edge.getLine();
                        manager.disconnect(line.getFromPos(), line.getToPos());
                    }
                }
            }
        });
        return null;
    }
}
