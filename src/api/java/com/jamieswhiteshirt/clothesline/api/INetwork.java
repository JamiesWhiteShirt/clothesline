package com.jamieswhiteshirt.clothesline.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;

import java.util.UUID;

public interface INetwork {
    int getId();

    UUID getUuid();

    AbsoluteNetworkState getState();

    void setState(AbsoluteNetworkState state);

    void update();

    boolean useItem(EntityPlayer player, EnumHand hand, int attachmentKey);

    void hitAttachment(EntityPlayer player, int attachmentKey);

    ItemStack insertItem(int attachmentKey, ItemStack stack, boolean simulate);

    ItemStack extractItem(int attachmentKey, boolean simulate);

    void setAttachment(int attachmentKey, ItemStack stack);

    void addEventListener(ResourceLocation key, INetworkEventListener eventListener);

    void removeEventListener(ResourceLocation key);
}
