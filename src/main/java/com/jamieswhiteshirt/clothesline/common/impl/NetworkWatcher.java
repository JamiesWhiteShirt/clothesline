package com.jamieswhiteshirt.clothesline.common.impl;

import com.google.common.collect.LinkedHashMultiset;
import com.google.common.collect.Multiset;
import com.jamieswhiteshirt.clothesline.api.INetwork;
import com.jamieswhiteshirt.clothesline.api.INetworkListener;
import com.jamieswhiteshirt.clothesline.common.network.message.AddNetworkMessage;
import com.jamieswhiteshirt.clothesline.common.network.message.RemoveAttachmentMessage;
import com.jamieswhiteshirt.clothesline.common.network.message.RemoveNetworkMessage;
import com.jamieswhiteshirt.clothesline.common.network.message.SetAttachmentMessage;
import com.jamieswhiteshirt.clothesline.common.util.BasicAttachment;
import com.jamieswhiteshirt.clothesline.common.util.BasicNetwork;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public final class NetworkWatcher implements INetworkListener {
    private final INetwork network;
    private final SimpleNetworkWrapper networkChannel;
    private final Multiset<EntityPlayerMP> players = LinkedHashMultiset.create();

    public NetworkWatcher(INetwork network, SimpleNetworkWrapper networkChannel) {
        this.network = network;
        this.networkChannel = networkChannel;
    }

    @Override
    public void onAttachmentChanged(INetwork network, int attachmentKey, ItemStack previousStack, ItemStack newStack) {
        if (!ItemStack.areItemStacksEqual(previousStack, newStack)) {
            IMessage message;
            if (!newStack.isEmpty()) {
                message = new SetAttachmentMessage(network.getId(), new BasicAttachment(attachmentKey, newStack));
            } else {
                message = new RemoveAttachmentMessage(network.getId(), attachmentKey);
            }

            sendToAllPlayers(message);
        }
    }

    public void addPlayer(EntityPlayerMP player) {
        if (players.add(player, 1) == 0) {
            networkChannel.sendTo(new AddNetworkMessage(BasicNetwork.fromAbsolute(network)), player);
        }
    }

    public void removePlayer(EntityPlayerMP player) {
        if (players.remove(player, 1) == 1) {
            networkChannel.sendTo(new RemoveNetworkMessage(network.getId()), player);
        }
    }

    private void sendToAllPlayers(IMessage message) {
        for (EntityPlayerMP player : players.elementSet()) {
            networkChannel.sendTo(message, player);
        }
    }

    public void clear() {
        sendToAllPlayers(new RemoveNetworkMessage(network.getId()));
        players.clear();
    }
}
