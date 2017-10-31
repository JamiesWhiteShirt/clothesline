package com.jamieswhiteshirt.clothesline.common.impl;

import com.jamieswhiteshirt.clothesline.api.INetworkManager;
import com.jamieswhiteshirt.clothesline.api.Network;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

public final class NetworkItemHandler implements IItemHandler {
    private final INetworkManager manager;
    private final Network network;
    private final int offset;

    public NetworkItemHandler(INetworkManager manager, Network network, int offset) {
        this.manager = manager;
        this.network = network;
        this.offset = offset;
    }

    @Override
    public int getSlots() {
        return 1;
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return network.getState().getAttachment(offset);
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        return manager.insertItem(network, offset, stack, simulate);
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return manager.extractItem(network, offset, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }
}
