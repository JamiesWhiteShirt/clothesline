package com.jamieswhiteshirt.clothesline.api;

import net.minecraft.item.ItemStack;

public interface INetworkEventListener {
    void onStateChanged(INetwork network, INetworkState previousState, INetworkState newState);

    void onAttachmentChanged(INetwork network, int attachmentKey, ItemStack previousStack, ItemStack newStack);
}
