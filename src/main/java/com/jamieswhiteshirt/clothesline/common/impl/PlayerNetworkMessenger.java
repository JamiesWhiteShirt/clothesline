package com.jamieswhiteshirt.clothesline.common.impl;

import com.jamieswhiteshirt.clothesline.api.INetwork;
import com.jamieswhiteshirt.clothesline.common.network.message.AddNetworkMessage;
import com.jamieswhiteshirt.clothesline.common.network.message.RemoveAttachmentMessage;
import com.jamieswhiteshirt.clothesline.common.network.message.RemoveNetworkMessage;
import com.jamieswhiteshirt.clothesline.common.network.message.SetAttachmentMessage;
import com.jamieswhiteshirt.clothesline.common.util.BasicAttachment;
import com.jamieswhiteshirt.clothesline.common.util.BasicNetwork;
import com.jamieswhiteshirt.clothesline.internal.INetworkMessenger;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public class PlayerNetworkMessenger implements INetworkMessenger<EntityPlayerMP> {
    private final SimpleNetworkWrapper networkChannel;

    public PlayerNetworkMessenger(SimpleNetworkWrapper networkChannel) {
        this.networkChannel = networkChannel;
    }

    @Override
    public void addNetwork(EntityPlayerMP watcher, INetwork network) {
        networkChannel.sendTo(new AddNetworkMessage(BasicNetwork.fromAbsolute(network)), watcher);
    }

    @Override
    public void removeNetwork(EntityPlayerMP watcher, INetwork network) {
        networkChannel.sendTo(new RemoveNetworkMessage(network.getId()), watcher);
    }

    @Override
    public void setAttachment(EntityPlayerMP watcher, INetwork network, int attachmentKey, ItemStack stack) {
        if (stack.isEmpty()) {
            networkChannel.sendTo(new RemoveAttachmentMessage(network.getId(), attachmentKey), watcher);
        } else {
            networkChannel.sendTo(new SetAttachmentMessage(network.getId(), new BasicAttachment(attachmentKey, stack)), watcher);
        }
    }
}
