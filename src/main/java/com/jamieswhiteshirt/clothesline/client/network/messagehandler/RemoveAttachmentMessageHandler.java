package com.jamieswhiteshirt.clothesline.client.network.messagehandler;

import com.jamieswhiteshirt.clothesline.Clothesline;
import com.jamieswhiteshirt.clothesline.api.INetwork;
import com.jamieswhiteshirt.clothesline.api.INetworkManager;
import com.jamieswhiteshirt.clothesline.common.network.message.RemoveAttachmentMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

@SideOnly(Side.CLIENT)
public class RemoveAttachmentMessageHandler implements IMessageHandler<RemoveAttachmentMessage, IMessage> {
    @Override
    @Nullable
    public IMessage onMessage(RemoveAttachmentMessage message, MessageContext ctx) {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            WorldClient world = Minecraft.getMinecraft().world;
            if (world != null) {
                INetworkManager manager = world.getCapability(Clothesline.NETWORK_MANAGER_CAPABILITY, null);
                if (manager != null) {
                    INetwork network = manager.getNetworks().getById(message.networkId);
                    if (network != null) {
                        network.setAttachment(message.attachmentKey, ItemStack.EMPTY);
                    }
                }
            }
        });
        return null;
    }
}
