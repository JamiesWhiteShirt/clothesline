package com.jamieswhiteshirt.clothesline.api;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.UUID;

public interface INetwork {
    int getId();

    UUID getUuid();

    AbsoluteNetworkState getState();

    void setState(AbsoluteNetworkState state);

    void update();

    ItemStack insertItem(int attachmentKey, ItemStack stack, boolean simulate);

    ItemStack extractItem(int attachmentKey, boolean simulate);

    void setAttachment(int attachmentKey, ItemStack stack);

    void addEventListener(ResourceLocation key, INetworkEventListener eventListener);

    void removeEventListener(ResourceLocation key);
}
