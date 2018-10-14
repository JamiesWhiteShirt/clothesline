package com.jamieswhiteshirt.clothesline.common.impl;

import com.jamieswhiteshirt.clothesline.api.*;
import com.jamieswhiteshirt.clothesline.common.Util;
import com.jamieswhiteshirt.clothesline.common.util.NetworkStateBuilder;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public abstract class NetworkManager implements INetworkManager {
    private final World world;
    private final INetworkCollection networks;

    protected NetworkManager(World world, INetworkCollection networks) {
        this.world = world;
        this.networks = networks;
    }

    protected abstract void createNetwork(INetworkState networkState);

    protected abstract void deleteNetwork(INetwork network);

    protected abstract void dropItems(INetworkState state, boolean dropClotheslines);

    @Override
    public INetworkCollection getNetworks() {
        return networks;
    }

    @Override
    public final void update() {
        world.profiler.startSection("tickClotheslines");
        networks.getValues().forEach(INetwork::update);
        world.profiler.endSection();
    }

    private void extend(INetwork network, BlockPos fromPos, BlockPos toPos) {
        NetworkStateBuilder stateBuilder = NetworkStateBuilder.fromAbsolute(network.getState());
        stateBuilder.addEdge(fromPos, toPos);

        deleteNetwork(network);
        createNetwork(stateBuilder.build());
    }

    @Override
    public final boolean connect(BlockPos fromPos, BlockPos toPos) {
        if (fromPos.equals(toPos)) {
            INetworkNode node = networks.getNodes().get(fromPos);
            if (node != null) {
                INetwork network = node.getNetwork();
                NetworkStateBuilder stateBuilder = NetworkStateBuilder.fromAbsolute(network.getState());
                stateBuilder.reroot(toPos);

                deleteNetwork(network);
                createNetwork(stateBuilder.build());
            }
            return false;
        }

        INetworkNode fromNode = networks.getNodes().get(fromPos);
        INetworkNode toNode = networks.getNodes().get(toPos);

        if (fromNode != null) {
            INetwork fromNetwork = fromNode.getNetwork();
            if (toNode != null) {
                INetwork toNetwork = toNode.getNetwork();

                if (fromNetwork == toNetwork) {
                    //TODO: Look into circular networks
                    return false;
                }

                deleteNetwork(fromNetwork);
                deleteNetwork(toNetwork);

                NetworkStateBuilder fromState = NetworkStateBuilder.fromAbsolute(fromNetwork.getState());
                NetworkStateBuilder toState = NetworkStateBuilder.fromAbsolute(toNetwork.getState());
                toState.reroot(toPos);
                fromState.addSubState(fromPos, toState);

                createNetwork(fromState.build());
            } else {
                extend(fromNetwork, fromPos, toPos);
            }
        } else {
            if (toNode != null) {
                INetwork toNetwork = toNode.getNetwork();
                extend(toNetwork, toPos, fromPos);
            } else {
                NetworkStateBuilder stateBuilder = NetworkStateBuilder.emptyRoot(0, fromPos);
                stateBuilder.addEdge(fromPos, toPos);
                createNetwork(stateBuilder.build());
            }
        }

        return true;
    }

    @Override
    public boolean breakConnection(@Nullable EntityLivingBase entity, BlockPos posA, BlockPos posB) {
        if (posA.equals(posB)) {
            return false;
        }

        INetworkNode nodeA = networks.getNodes().get(posA);
        INetworkNode nodeB = networks.getNodes().get(posB);
        if (nodeA != null && nodeB != null) {
            INetwork network = nodeA.getNetwork();
            if (network == nodeB.getNetwork()) {
                NetworkStateBuilder state = NetworkStateBuilder.fromAbsolute(network.getState());
                state.reroot(posA);
                deleteNetwork(network);
                applySplitResult(state.splitEdge(posB), !Util.isCreativePlayer(entity));

                return true;
            }
        }

        return false;
    }

    private void applySplitResult(NetworkStateBuilder.SplitResult splitResult, boolean dropClotheslines) {
        for (NetworkStateBuilder subState : splitResult.getSubStates()) {
            createNetwork(subState.build());
        }
        dropItems(splitResult.getState().build(), dropClotheslines);
    }

    @Override
    public void createNode(BlockPos pos) {
        NetworkStateBuilder stateBuilder = NetworkStateBuilder.emptyRoot(0, pos);
        createNetwork(stateBuilder.build());
    }

    @Override
    public final void breakNode(@Nullable EntityLivingBase entity, BlockPos pos) {
        INetworkNode node = networks.getNodes().get(pos);
        if (node != null) {
            INetwork network = node.getNetwork();
            NetworkStateBuilder state = NetworkStateBuilder.fromAbsolute(network.getState());
            state.reroot(pos);
            deleteNetwork(network);
            applySplitResult(state.splitRoot(), !Util.isCreativePlayer(entity));
        }
    }
}
