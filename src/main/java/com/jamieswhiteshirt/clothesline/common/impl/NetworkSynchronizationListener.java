package com.jamieswhiteshirt.clothesline.common.impl;

import com.jamieswhiteshirt.clothesline.api.AbsoluteNetworkState;
import com.jamieswhiteshirt.clothesline.api.INetworkEventListener;
import com.jamieswhiteshirt.clothesline.common.network.message.RemoveAttachmentMessage;
import com.jamieswhiteshirt.clothesline.common.network.message.SetAttachmentMessage;
import com.jamieswhiteshirt.clothesline.common.network.message.SetNetworkStateMessage;
import com.jamieswhiteshirt.clothesline.common.util.BasicAttachment;
import com.jamieswhiteshirt.clothesline.common.util.BasicNetworkState;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public class NetworkSynchronizationListener implements INetworkEventListener {
    private final int networkId;
    private final int dimension;
    private final SimpleNetworkWrapper networkWrapper;


    public NetworkSynchronizationListener(int networkId, int dimension, SimpleNetworkWrapper networkWrapper) {
        this.networkId = networkId;
        this.dimension = dimension;
        this.networkWrapper = networkWrapper;
    }

    @Override
    public void onStateChanged(AbsoluteNetworkState previousState, AbsoluteNetworkState newState) {
        networkWrapper.sendToDimension(new SetNetworkStateMessage(networkId, BasicNetworkState.fromAbsolute(newState)), dimension);
    }

    @Override
    public void onAttachmentChanged(int attachmentKey, ItemStack previousStack, ItemStack newStack) {
        if (!ItemStack.areItemStacksEqual(previousStack, newStack)) {
            if (!newStack.isEmpty()) {
                networkWrapper.sendToDimension(new SetAttachmentMessage(networkId, new BasicAttachment(attachmentKey, newStack)), dimension);
            } else {
                networkWrapper.sendToDimension(new RemoveAttachmentMessage(networkId, attachmentKey), dimension);
            }
        }
    }
}
