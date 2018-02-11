package com.jamieswhiteshirt.clothesline.common.impl;

import com.jamieswhiteshirt.clothesline.api.INetwork;
import com.jamieswhiteshirt.clothesline.api.INetworkManagerEventListener;
import com.jamieswhiteshirt.clothesline.common.network.message.*;
import com.jamieswhiteshirt.clothesline.common.util.BasicNetwork;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

import java.util.List;
import java.util.stream.Collectors;

public class ManagerSynchronizationListener implements INetworkManagerEventListener {
    //TODO: This is currently syncing all networks in a dimension. This has to be tailored for each player
    private static final ResourceLocation SYNCHRONIZATION_KEY = new ResourceLocation("clothesline", "synchronization");

    private final int dimension;
    private final SimpleNetworkWrapper networkChannel;

    public ManagerSynchronizationListener(WorldServer world, SimpleNetworkWrapper networkChannel) {
        dimension = world.provider.getDimension();
        this.networkChannel = networkChannel;
    }

    @Override
    public void onNetworksReset(List<INetwork> previousNetworks, List<INetwork> newNetworks) {
        networkChannel.sendToDimension(new SetNetworkMessage(newNetworks.stream().map(
                BasicNetwork::fromAbsolute
        ).collect(Collectors.toList())), dimension);
    }

    @Override
    public void onNetworkAdded(INetwork network) {
        networkChannel.sendToDimension(new AddNetworkMessage(BasicNetwork.fromAbsolute(network)), dimension);
        network.addEventListener(SYNCHRONIZATION_KEY, new NetworkSynchronizationListener(network.getId(), dimension, networkChannel));
    }

    @Override
    public void onNetworkRemoved(INetwork network) {
        networkChannel.sendToDimension(new RemoveNetworkMessage(network.getId()), dimension);
        network.removeEventListener(SYNCHRONIZATION_KEY);
    }
}
