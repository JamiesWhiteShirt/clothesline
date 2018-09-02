package com.jamieswhiteshirt.clothesline.api;

import net.minecraft.item.ItemStack;

public interface INetworkEventListener {
    void onAttachmentChanged(INetwork network, int attachmentKey, ItemStack previousStack, ItemStack newStack);
}
