package com.jamieswhiteshirt.clothesline.common.network.messagehandler;

import com.jamieswhiteshirt.clothesline.Clothesline;
import com.jamieswhiteshirt.clothesline.api.INetworkManager;
import com.jamieswhiteshirt.clothesline.api.Network;
import com.jamieswhiteshirt.clothesline.common.Util;
import com.jamieswhiteshirt.clothesline.common.network.message.MessageRemoveAttachment;
import com.jamieswhiteshirt.clothesline.common.network.message.MessageSetAttachment;
import com.jamieswhiteshirt.clothesline.common.network.message.MessageTryUseItemOnNetwork;
import com.jamieswhiteshirt.clothesline.common.util.BasicAttachment;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nullable;

public class MessageTryUseItemOnNetworkHandler implements IMessageHandler<MessageTryUseItemOnNetwork, IMessage> {
    @CapabilityInject(INetworkManager.class)
    private static final Capability<INetworkManager> CLOTHESLINE_NETWORK_MANAGER_CAPABILITY = Util.nonNullInjected();

    @Nullable
    @Override
    public IMessage onMessage(MessageTryUseItemOnNetwork message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().player;
        WorldServer world = player.getServerWorld();
        world.addScheduledTask(() -> {
            INetworkManager manager = world.getCapability(CLOTHESLINE_NETWORK_MANAGER_CAPABILITY, null);
            if (manager != null) {
                //TODO: Validate this edge position
                Network network = manager.getNetworkByUUID(message.networkUuid);
                if (network != null) {
                    manager.useItem(network, player, message.hand, message.offset);

                    // The client may have made an incorrect assumption.
                    // Send the current attachment to make sure the client keeps up.
                    ItemStack stack = network.getState().getAttachment(message.offset);
                    if (!stack.isEmpty()) {
                        Clothesline.instance.networkWrapper.sendTo(new MessageSetAttachment(network.getUuid(), new BasicAttachment(message.offset, stack)), player);
                    } else {
                        Clothesline.instance.networkWrapper.sendTo(new MessageRemoveAttachment(network.getUuid(), message.offset), player);
                    }
                }
            }
        });
        return null;
    }
}