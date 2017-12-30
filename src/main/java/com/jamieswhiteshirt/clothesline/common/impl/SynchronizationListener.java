package com.jamieswhiteshirt.clothesline.common.impl;

import com.jamieswhiteshirt.clothesline.api.AbsoluteNetworkState;
import com.jamieswhiteshirt.clothesline.api.INetworkManagerEventListener;
import com.jamieswhiteshirt.clothesline.api.Network;
import com.jamieswhiteshirt.clothesline.common.network.message.*;
import com.jamieswhiteshirt.clothesline.common.util.BasicAttachment;
import com.jamieswhiteshirt.clothesline.common.util.BasicNetwork;
import com.jamieswhiteshirt.clothesline.common.util.BasicNetworkState;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

import java.util.Collection;
import java.util.stream.Collectors;

public class SynchronizationListener implements INetworkManagerEventListener {
    //TODO: This is currently syncing all networks in a dimension. This has to be tailored for each player
    private final int dimension;
    private final SimpleNetworkWrapper networkWrapper;

    public SynchronizationListener(WorldServer world, SimpleNetworkWrapper networkWrapper) {
        dimension = world.provider.getDimension();
        this.networkWrapper = networkWrapper;
    }

    @Override
    public void onNetworksSet(Collection<Network> networks) {
        networkWrapper.sendToDimension(new MessageSetNetworks(networks.stream().map(
                BasicNetwork::fromAbsolute
        ).collect(Collectors.toList())), dimension);
    }

    @Override
    public void onNetworkAdded(Network network) {
        networkWrapper.sendToDimension(new MessageAddNetwork(BasicNetwork.fromAbsolute(network)), dimension);
    }

    @Override
    public void onNetworkRemoved(Network network) {
        networkWrapper.sendToDimension(new MessageRemoveNetwork(network.getUuid()), dimension);
    }

    @Override
    public void onNetworkStateChanged(Network network, AbsoluteNetworkState state) {
        networkWrapper.sendToDimension(new MessageSetNetworkState(network.getUuid(), BasicNetworkState.fromAbsolute(state)), dimension);
    }

    @Override
    public void onAttachmentChanged(Network network, int attachmentKey, ItemStack previousStack, ItemStack newStack) {
        if (!ItemStack.areItemStacksEqual(previousStack, newStack)) {
            if (!newStack.isEmpty()) {
                networkWrapper.sendToDimension(new MessageSetAttachment(network.getUuid(), new BasicAttachment(attachmentKey, newStack)), dimension);
            } else {
                networkWrapper.sendToDimension(new MessageRemoveAttachment(network.getUuid(), attachmentKey), dimension);
            }
        }
    }
}
