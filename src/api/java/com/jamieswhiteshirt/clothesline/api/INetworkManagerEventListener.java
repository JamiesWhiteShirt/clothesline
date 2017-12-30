package com.jamieswhiteshirt.clothesline.api;

import net.minecraft.item.ItemStack;

import java.util.Collection;

public interface INetworkManagerEventListener {
    void onNetworksSet(Collection<Network> networks);

    void onNetworkAdded(Network network);

    void onNetworkRemoved(Network network);

    void onNetworkStateChanged(Network network, AbsoluteNetworkState state);

    void onAttachmentChanged(Network network, int attachmentKey, ItemStack previousStack, ItemStack newStack);
}
