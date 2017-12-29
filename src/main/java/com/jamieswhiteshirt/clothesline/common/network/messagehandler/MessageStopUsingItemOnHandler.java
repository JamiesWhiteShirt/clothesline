package com.jamieswhiteshirt.clothesline.common.network.messagehandler;

import com.jamieswhiteshirt.clothesline.common.item.ItemConnector;
import com.jamieswhiteshirt.clothesline.common.network.message.MessageStopUsingItemOn;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nullable;

public class MessageStopUsingItemOnHandler implements IMessageHandler<MessageStopUsingItemOn, IMessage> {
    @Nullable
    @Override
    public IMessage onMessage(MessageStopUsingItemOn message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().player;
        WorldServer world = player.getServerWorld();
        world.addScheduledTask(() -> {
            //TODO: Validate this position
            if (player.getActiveItemStack().getItem() instanceof ItemConnector) {
                ItemConnector itemConnector = (ItemConnector) player.getActiveItemStack().getItem();
                itemConnector.stopActiveHandWithToPos(player, message.pos);
            } else {
                player.stopActiveHand();
            }
        });
        return null;
    }
}
