package com.jamieswhiteshirt.clothesline.api;

import net.minecraft.item.ItemStack;

public interface INetworkEventListener {
    void onStateChanged(AbsoluteNetworkState previousState, AbsoluteNetworkState newState);

    void onAttachmentChanged(int attachmentKey, ItemStack previousStack, ItemStack newStack);
}
