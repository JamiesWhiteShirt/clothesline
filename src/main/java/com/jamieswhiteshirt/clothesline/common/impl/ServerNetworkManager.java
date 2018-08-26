package com.jamieswhiteshirt.clothesline.common.impl;

import com.jamieswhiteshirt.clothesline.api.*;
import com.jamieswhiteshirt.clothesline.api.util.MutableSortedIntMap;
import com.jamieswhiteshirt.clothesline.common.util.NetworkStateBuilder;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public final class ServerNetworkManager extends NetworkManager<INetworkEdge, INetworkNode> implements IServerNetworkManager {
    private final WorldServer world;
    private Map<UUID, INetwork> networksByUuid = new HashMap<>();
    private int nextNetworkId = 0;

    public ServerNetworkManager(WorldServer world) {
        super(world);
        this.world = world;
    }

    private void dropAttachment(INetworkState state, ItemStack stack, int attachmentKey) {
        if (!stack.isEmpty() && world.getGameRules().getBoolean("doTileDrops")) {
            Vec3d pos = state.getPath().getPositionForOffset(state.attachmentKeyToOffset(attachmentKey));
            EntityItem entityitem = new EntityItem(world, pos.x, pos.y - 0.5D, pos.z, stack);
            entityitem.setDefaultPickupDelay();
            world.spawnEntity(entityitem);
        }
    }

    private void dropNetworkItems(INetworkState state) {
        for (MutableSortedIntMap.Entry<ItemStack> entry : state.getAttachments().entries()) {
            dropAttachment(state, entry.getValue(), entry.getKey());
        }
    }

    private Network createAndAddNetwork(INetworkState state) {
        Network network = new Network(nextNetworkId++, new PersistentNetwork(UUID.randomUUID(), state));
        addNetwork(network);
        return network;
    }

    @Override
    public void reset(List<PersistentNetwork> data) {
        nextNetworkId = 0;
        List<INetwork> networks = data.stream().map(persistent -> new Network(nextNetworkId++, persistent)).collect(Collectors.toList());
        this.networksByUuid = new HashMap<>();
        for (INetwork network : networks) {
            networksByUuid.put(network.getUuid(), network);
        }
        resetInternal(networks);
    }

    @Override
    protected INetworkEdge createNetworkEdge(Path.Edge pathEdge, INetwork network, int index) {
        return new NetworkEdge(network, pathEdge, index);
    }

    @Override
    protected INetworkNode createNetworkNode(Path.Node pathNode, INetwork network) {
        return new NetworkNode(network, pathNode);
    }

    @Override
    protected void addNetwork(INetwork network) {
        networksByUuid.put(network.getUuid(), network);
        super.addNetwork(network);
    }

    @Override
    public void removeNetwork(INetwork network) {
        networksByUuid.remove(network.getUuid());
        super.removeNetwork(network);
    }

    @Nullable
    @Override
    public final INetwork getNetworkByUuid(UUID uuid) {
        return networksByUuid.get(uuid);
    }

    private void extend(INetwork network, BlockPos fromPos, BlockPos toPos) {
        NetworkStateBuilder relativeState = NetworkStateBuilder.fromAbsolute(network.getState());
        relativeState.addEdge(fromPos, toPos);
        network.setState(relativeState.toAbsolute());
    }

    @Override
    public final boolean connect(BlockPos fromPos, BlockPos toPos) {
        if (fromPos.equals(toPos)) {
            INetworkNode node = getNodes().get(fromPos);
            if (node != null) {
                INetwork network = node.getNetwork();
                NetworkStateBuilder relativeState = NetworkStateBuilder.fromAbsolute(network.getState());
                relativeState.reroot(toPos);
                network.setState(relativeState.toAbsolute());
            }
            return false;
        }

        INetworkNode fromNode = getNodes().get(fromPos);
        INetworkNode toNode = getNodes().get(toPos);

        if (fromNode != null) {
            INetwork fromNetwork = fromNode.getNetwork();
            if (toNode != null) {
                INetwork toNetwork = toNode.getNetwork();

                if (fromNetwork == toNetwork) {
                    //TODO: Look into circular networks
                    return false;
                }

                removeNetwork(fromNetwork);
                removeNetwork(toNetwork);

                NetworkStateBuilder fromState = NetworkStateBuilder.fromAbsolute(fromNetwork.getState());
                NetworkStateBuilder toState = NetworkStateBuilder.fromAbsolute(toNetwork.getState());
                toState.reroot(toPos);
                fromState.addSubState(fromPos, toState);

                createAndAddNetwork(fromState.toAbsolute());
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
                createAndAddNetwork(stateBuilder.toAbsolute());
            }
        }

        return true;
    }

    @Override
    public boolean disconnect(BlockPos posA, BlockPos posB) {
        if (posA.equals(posB)) {
            return false;
        }

        INetworkNode nodeA = getNodes().get(posA);
        INetworkNode nodeB = getNodes().get(posB);
        if (nodeA != null && nodeB != null) {
            INetwork network = nodeA.getNetwork();
            if (network == nodeB.getNetwork()) {
                NetworkStateBuilder state = NetworkStateBuilder.fromAbsolute(network.getState());
                state.reroot(posA);
                removeNetwork(network);
                applySplitResult(state.splitEdge(posB));

                return true;
            }
        }

        return false;
    }

    private void applySplitResult(NetworkStateBuilder.SplitResult splitResult) {
        for (NetworkStateBuilder subState : splitResult.getSubStates()) {
            createAndAddNetwork(subState.toAbsolute());
        }
        dropNetworkItems(splitResult.getState().toAbsolute());
    }

    @Override
    public final void destroyNode(BlockPos pos) {
        INetworkNode node = getNodes().get(pos);
        if (node != null) {
            INetwork network = node.getNetwork();
            NetworkStateBuilder state = NetworkStateBuilder.fromAbsolute(network.getState());
            state.reroot(pos);
            removeNetwork(network);
            applySplitResult(state.splitRoot());
        }
    }

    @Override
    public void createNode(BlockPos pos) {
        NetworkStateBuilder stateBuilder = NetworkStateBuilder.emptyRoot(0, pos);
        createAndAddNetwork(stateBuilder.toAbsolute());
    }
}
