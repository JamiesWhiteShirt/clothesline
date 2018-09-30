package com.jamieswhiteshirt.clothesline.internal;

import com.jamieswhiteshirt.clothesline.api.INetwork;
import net.minecraft.item.ItemStack;

public interface INetworkMessenger<T> {
    void addNetwork(T watcher, INetwork network);

    void removeNetwork(T watcher, INetwork network);

    void setAttachment(T watcher, INetwork network, int attachmentKey, ItemStack stack);

    void setShiftAndMomentum(T watcher, INetwork network, int shift, int momentum);
}
