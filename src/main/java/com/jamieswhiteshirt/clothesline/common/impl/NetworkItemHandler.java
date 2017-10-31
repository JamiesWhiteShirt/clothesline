package com.jamieswhiteshirt.clothesline.common.impl;

import com.jamieswhiteshirt.clothesline.api.INetworkManager;
import com.jamieswhiteshirt.clothesline.api.Measurements;
import com.jamieswhiteshirt.clothesline.api.Network;
import com.jamieswhiteshirt.clothesline.api.util.MutableSortedIntMap;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import java.util.List;

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

    private int getCandidateOffset() {
        List<MutableSortedIntMap.Entry<ItemStack>> entries = network.getState().getAttachmentsInRange(
                offset - Measurements.UNIT_LENGTH / 2,
                offset + Measurements.UNIT_LENGTH / 2
        );

        if (entries.isEmpty()) {
            return offset;
        } else {
            return entries.get(entries.size() / 2).getKey();
        }
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return network.getState().getAttachment(getCandidateOffset());
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        return manager.insertItem(network, getCandidateOffset(), stack, simulate);
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return manager.extractItem(network, getCandidateOffset(), simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }
}
