package com.jamieswhiteshirt.clothesline.common.impl;

import com.jamieswhiteshirt.clothesline.api.INetwork;
import com.jamieswhiteshirt.clothesline.api.AttachmentUnit;
import com.jamieswhiteshirt.clothesline.api.util.MutableSortedIntMap;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import java.util.List;

public final class NetworkItemHandler implements IItemHandler {
    private final INetwork network;
    private final int attachmentKey;

    public NetworkItemHandler(INetwork network, int attachmentKey) {
        this.network = network;
        this.attachmentKey = attachmentKey;
    }

    @Override
    public int getSlots() {
        return 1;
    }

    private int getCandidateAttachmentKey() {
        List<MutableSortedIntMap.Entry<ItemStack>> entries = network.getState().getAttachmentsInRange(
                attachmentKey - AttachmentUnit.UNITS_PER_BLOCK / 2,
                attachmentKey + AttachmentUnit.UNITS_PER_BLOCK / 2
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
        return network.insertItem(getCandidateAttachmentKey(), stack, simulate);
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return network.extractItem(getCandidateAttachmentKey(), simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }
}
