package com.jamieswhiteshirt.clothesline.api;

import net.minecraft.item.ItemStack;

import java.util.List;

public interface INetworkManagerEventListener {
    void onNetworksReset(List<Network> networks);

    void onNetworkAdded(Network network);

    void onNetworkRemoved(Network network);

    void onNetworkStateChanged(Network network, AbsoluteNetworkState state);

    void onAttachmentChanged(Network network, int attachmentKey, ItemStack previousStack, ItemStack newStack);
}
