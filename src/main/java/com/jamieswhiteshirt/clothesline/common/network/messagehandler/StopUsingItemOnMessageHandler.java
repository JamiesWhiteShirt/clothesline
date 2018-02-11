package com.jamieswhiteshirt.clothesline.common.network.messagehandler;

import com.jamieswhiteshirt.clothesline.api.Measurements;
import com.jamieswhiteshirt.clothesline.common.item.ItemConnector;
import com.jamieswhiteshirt.clothesline.common.network.message.StopUsingItemOnMessage;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nullable;

public class StopUsingItemOnMessageHandler implements IMessageHandler<StopUsingItemOnMessage, IMessage> {
    @Nullable
    @Override
    public IMessage onMessage(StopUsingItemOnMessage message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().player;
        WorldServer world = player.getServerWorld();
        world.addScheduledTask(() -> {
            if (player.getActiveItemStack().getItem() instanceof ItemConnector) {
                ItemConnector itemConnector = (ItemConnector) player.getActiveItemStack().getItem();
                if (Validation.canReachPos(player, Measurements.midVec(message.pos))) {
                    itemConnector.stopActiveHandWithToPos(player, message.pos);
                } else {
                    player.stopActiveHand();
                }
            } else {
                player.stopActiveHand();
            }
        });
        return null;
    }
}
