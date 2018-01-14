package com.jamieswhiteshirt.clothesline.common.impl;

import com.jamieswhiteshirt.clothesline.api.ICommonNetworkManager;
import com.jamieswhiteshirt.clothesline.api.Measurements;
import com.jamieswhiteshirt.clothesline.api.Network;
import com.jamieswhiteshirt.clothesline.api.util.MutableSortedIntMap;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import java.util.List;

public final class NetworkItemHandler implements IItemHandler {
    private final ICommonNetworkManager manager;
    private final Network network;
    private final int attachmentKey;

    public NetworkItemHandler(ICommonNetworkManager manager, Network network, int attachmentKey) {
        this.manager = manager;
        this.network = network;
        this.attachmentKey = attachmentKey;
    }

    @Override
    public int getSlots() {
        return 1;
    }

    private int getCandidateAttachmentKey() {
        List<MutableSortedIntMap.Entry<ItemStack>> entries = network.getState().getAttachmentsInRange(
                attachmentKey - Measurements.UNIT_LENGTH / 2,
                attachmentKey + Measurements.UNIT_LENGTH / 2
        );

        if (entries.isEmpty()) {
            return attachmentKey;
        } else {
            return entries.get(entries.size() / 2).getKey();
        }
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return network.getState().getAttachment(getCandidateAttachmentKey());
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        return manager.insertItem(network, getCandidateAttachmentKey(), stack, simulate);
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return manager.extractItem(network, getCandidateAttachmentKey(), simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }
}
