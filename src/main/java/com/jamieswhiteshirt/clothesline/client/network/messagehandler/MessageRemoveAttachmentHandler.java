package com.jamieswhiteshirt.clothesline.client.network.messagehandler;

import com.jamieswhiteshirt.clothesline.api.Network;
import com.jamieswhiteshirt.clothesline.api.client.IClientNetworkManager;
import com.jamieswhiteshirt.clothesline.common.Util;
import com.jamieswhiteshirt.clothesline.common.network.message.MessageRemoveAttachment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

@SideOnly(Side.CLIENT)
public class MessageRemoveAttachmentHandler implements IMessageHandler<MessageRemoveAttachment, IMessage> {
    @CapabilityInject(IClientNetworkManager.class)
    private static final Capability<IClientNetworkManager> NETWORK_MANAGER_CAPABILITY = Util.nonNullInjected();

    @Override
    @Nullable
    public IMessage onMessage(MessageRemoveAttachment message, MessageContext ctx) {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            WorldClient world = Minecraft.getMinecraft().world;
            if (world != null) {
                IClientNetworkManager manager = world.getCapability(NETWORK_MANAGER_CAPABILITY, null);
                if (manager != null) {
                    Network network = manager.getNetworkById(message.networkId);
                    if (network != null) {
                        manager.setAttachment(network, message.attachmentKey, ItemStack.EMPTY);
                    }
                }
            }
        });
        return null;
    }
}
