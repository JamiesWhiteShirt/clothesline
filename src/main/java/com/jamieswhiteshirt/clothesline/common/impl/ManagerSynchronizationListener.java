package com.jamieswhiteshirt.clothesline.common.impl;

import com.jamieswhiteshirt.clothesline.api.*;
import com.jamieswhiteshirt.clothesline.common.network.message.*;
import com.jamieswhiteshirt.clothesline.common.util.BasicNetwork;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

import java.util.List;
import java.util.stream.Collectors;

public class ManagerSynchronizationListener<T extends INetworkEdge> implements INetworkManagerEventListener<T> {
    //TODO: This is currently syncing all networks in a dimension. This has to be tailored for each player
    private static final ResourceLocation SYNCHRONIZATION_KEY = new ResourceLocation("clothesline", "synchronization");

    private final int dimension;
    private final SimpleNetworkWrapper networkChannel;
    private final INetworkEventListener networkSynchronizationListener;

    public ManagerSynchronizationListener(WorldServer world, SimpleNetworkWrapper networkChannel) {
        dimension = world.provider.getDimension();
        this.networkChannel = networkChannel;
        this.networkSynchronizationListener = new NetworkSynchronizationListener(dimension, networkChannel);
    }

    @Override
    public void onNetworksReset(INetworkManager<T> networkManager, List<INetwork> previousNetworks, List<INetwork> newNetworks) {
        networkChannel.sendToDimension(new SetNetworkMessage(newNetworks.stream().map(
                BasicNetwork::fromAbsolute
        ).collect(Collectors.toList())), dimension);
    }

    @Override
    public void onNetworkAdded(INetworkManager<T> networkManager, INetwork network) {
        networkChannel.sendToDimension(new AddNetworkMessage(BasicNetwork.fromAbsolute(network)), dimension);
        network.addEventListener(SYNCHRONIZATION_KEY, networkSynchronizationListener);
    }

    @Override
    public void onNetworkRemoved(INetworkManager<T> networkManager, INetwork network) {
        networkChannel.sendToDimension(new RemoveNetworkMessage(network.getId()), dimension);
        network.removeEventListener(SYNCHRONIZATION_KEY);
    }

    @Override
    public void onUpdate(INetworkManager<T> networkManager) {
    }
}
