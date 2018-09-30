package com.jamieswhiteshirt.clothesline.common.impl;

import com.google.common.collect.LinkedHashMultiset;
import com.google.common.collect.Multiset;
import com.jamieswhiteshirt.clothesline.api.INetwork;
import com.jamieswhiteshirt.clothesline.api.INetworkListener;
import com.jamieswhiteshirt.clothesline.internal.INetworkMessenger;
import net.minecraft.item.ItemStack;

public final class NetworkTracker<T> implements INetworkListener {
    private final INetwork network;
    private final INetworkMessenger<T> messenger;
    private final Multiset<T> watchers = LinkedHashMultiset.create();

    public NetworkTracker(INetwork network, INetworkMessenger<T> messenger) {
        this.network = network;
        this.messenger = messenger;
    }

    @Override
    public void onAttachmentChanged(INetwork network, int attachmentKey, ItemStack previousStack, ItemStack newStack) {
        if (!ItemStack.areItemStacksEqual(previousStack, newStack)) {
            for (T player : watchers.elementSet()) {
                messenger.setAttachment(player, network, attachmentKey, newStack);
            }
        }
    }

    public void addWatcher(T watcher) {
        if (watchers.add(watcher, 1) == 0) {
            messenger.addNetwork(watcher, network);
        }
    }

    public void removeWatcher(T watcher) {
        if (watchers.remove(watcher, 1) == 1) {
            messenger.removeNetwork(watcher, network);
        }
    }

    public void clear() {
        for (T player : watchers.elementSet()) {
            messenger.removeNetwork(player, network);
        }
        watchers.clear();
    }
}
