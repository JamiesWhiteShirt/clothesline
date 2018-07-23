package com.jamieswhiteshirt.clothesline.common;

import com.jamieswhiteshirt.clothesline.api.INetworkState;
import com.jamieswhiteshirt.clothesline.api.INetwork;
import com.jamieswhiteshirt.clothesline.api.INetworkEventListener;
import com.jamieswhiteshirt.clothesline.common.network.message.RemoveAttachmentMessage;
import com.jamieswhiteshirt.clothesline.common.network.message.SetAttachmentMessage;
import com.jamieswhiteshirt.clothesline.common.network.message.SetNetworkStateMessage;
import com.jamieswhiteshirt.clothesline.common.util.BasicAttachment;
import com.jamieswhiteshirt.clothesline.common.util.BasicNetworkState;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public class SyncNetworkListener implements INetworkEventListener {
    private final int dimension;
    private final SimpleNetworkWrapper networkChannel;


    public SyncNetworkListener(int dimension, SimpleNetworkWrapper networkChannel) {
        this.dimension = dimension;
        this.networkChannel = networkChannel;
    }

    @Override
    public void onStateChanged(INetwork network, INetworkState previousState, INetworkState newState) {
        networkChannel.sendToDimension(new SetNetworkStateMessage(network.getId(), BasicNetworkState.fromAbsolute(newState)), dimension);
    }

    @Override
    public void onAttachmentChanged(INetwork network, int attachmentKey, ItemStack previousStack, ItemStack newStack) {
        if (!ItemStack.areItemStacksEqual(previousStack, newStack)) {
            if (!newStack.isEmpty()) {
                networkChannel.sendToDimension(new SetAttachmentMessage(network.getId(), new BasicAttachment(attachmentKey, newStack)), dimension);
            } else {
                networkChannel.sendToDimension(new RemoveAttachmentMessage(network.getId(), attachmentKey), dimension);
            }
        }
    }
}
