package com.jamieswhiteshirt.clothesline.common.network.messagehandler;

import com.jamieswhiteshirt.clothesline.Clothesline;
import com.jamieswhiteshirt.clothesline.api.*;
import com.jamieswhiteshirt.clothesline.common.network.message.HitNetworkMessage;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
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
                INetwork network = manager.getNetworks().getById(message.networkId);
                if (network != null) {
                    Path.Edge edge = network.getState().getPath().getEdgeForPosition(message.offset);
                    Vec3d pos = edge.getPositionForOffset(message.offset);
                    if (Validation.canReachPos(player, pos)) {
                        Line line = edge.getLine();
                        manager.breakConnection(player, line.getFromPos(), line.getToPos());
                        world.playSound(player, pos.x, pos.y, pos.z, SoundEvents.ENTITY_LEASHKNOT_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    }
                }
            }
        });
        return null;
    }
}
