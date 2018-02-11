package com.jamieswhiteshirt.clothesline.common.network.messagehandler;

import com.jamieswhiteshirt.clothesline.Clothesline;
import com.jamieswhiteshirt.clothesline.api.IServerNetworkManager;
import com.jamieswhiteshirt.clothesline.api.Network;
import com.jamieswhiteshirt.clothesline.common.network.message.HitAttachmentMessage;
import com.jamieswhiteshirt.clothesline.common.network.message.RemoveAttachmentMessage;
import com.jamieswhiteshirt.clothesline.common.network.message.SetAttachmentMessage;
import com.jamieswhiteshirt.clothesline.common.util.BasicAttachment;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nullable;

public class HitAttachmentMessageHandler implements IMessageHandler<HitAttachmentMessage, IMessage> {
    @Nullable
    @Override
    public IMessage onMessage(HitAttachmentMessage message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().player;
        WorldServer world = player.getServerWorld();
        world.addScheduledTask(() -> {
            IServerNetworkManager manager = world.getCapability(Clothesline.SERVER_NETWORK_MANAGER_CAPABILITY, null);
            if (manager != null) {
                Network network = manager.getNetworkById(message.networkId);
                if (network != null) {
                    if (Validation.canReachAttachment(player, network, message.attachmentKey)) {
                        manager.hitAttachment(network, player, message.attachmentKey);
                    }

                    // The client may have made an incorrect assumption.
                    // Send the current attachment to make sure the client keeps up.
                    ItemStack stack = network.getState().getAttachment(message.attachmentKey);
                    if (!stack.isEmpty()) {
                        Clothesline.instance.networkChannel.sendTo(new SetAttachmentMessage(network.getId(), new BasicAttachment(message.attachmentKey, stack)), player);
                    } else {
                        Clothesline.instance.networkChannel.sendTo(new RemoveAttachmentMessage(network.getId(), message.attachmentKey), player);
                    }
                }
            }
        });
        return null;
    }
}
