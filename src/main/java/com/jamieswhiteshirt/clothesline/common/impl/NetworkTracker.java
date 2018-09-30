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
    private int lastShift;
    private int lastMomentum;

    public NetworkTracker(INetwork network, INetworkMessenger<T> messenger) {
        this.network = network;
        this.messenger = messenger;
        this.lastShift= network.getState().getShift();
        this.lastMomentum = network.getState().getMomentum();
    }

    @Override
    public void onAttachmentChanged(INetwork network, int attachmentKey, ItemStack previousStack, ItemStack newStack) {
        if (!ItemStack.areItemStacksEqual(previousStack, newStack)) {
            for (T watcher : watchers.elementSet()) {
                messenger.setAttachment(watcher, network, attachmentKey, newStack);
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
        for (T watcher : watchers.elementSet()) {
            messenger.removeNetwork(watcher, network);
        }
        watchers.clear();
    }

    public void update() {
        int shift = network.getState().getShift();
        int momentum = network.getState().getMomentum();

        if (shift != lastShift || momentum != lastMomentum) {
            for (T watcher : watchers.elementSet()) {
                messenger.setShiftAndMomentum(watcher, network, shift, momentum);
            }

            lastShift = shift;
            lastMomentum = momentum;
        }
    }
}
