package com.jamieswhiteshirt.clothesline.api;

import net.minecraft.item.ItemStack;

import java.util.Collection;

public interface INetworkManagerEventListener {
    void onNetworksSet(Collection<Network> networks);

    void onNetworkAdded(Network network);

    void onNetworkRemoved(Network network);

    void onAttachmentChanged(Network network, int offset, ItemStack previousStack, ItemStack newStack);
}
