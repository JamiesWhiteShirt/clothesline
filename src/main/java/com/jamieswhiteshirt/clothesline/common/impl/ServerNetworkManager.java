package com.jamieswhiteshirt.clothesline.common.impl;

import com.jamieswhiteshirt.clothesline.api.*;
import com.jamieswhiteshirt.clothesline.api.util.MutableSortedIntMap;
import com.jamieswhiteshirt.clothesline.common.util.BasicTree;
import com.jamieswhiteshirt.clothesline.common.util.RelativeNetworkState;
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

public final class ServerNetworkManager extends NetworkManager<NetworkEdge> implements IServerNetworkManager<NetworkEdge> {
    private final WorldServer world;
    private Map<UUID, INetwork> networksByUuid = new HashMap<>();
    private int nextNetworkId = 0;

    public ServerNetworkManager(WorldServer world) {
        super(world);
        this.world = world;
    }

    private void dropAttachment(AbsoluteNetworkState state, ItemStack stack, int attachmentKey) {
        if (!stack.isEmpty() && world.getGameRules().getBoolean("doTileDrops")) {
            Vec3d pos = state.getGraph().getPositionForOffset(state.attachmentKeyToOffset(attachmentKey));
            EntityItem entityitem = new EntityItem(world, pos.x, pos.y - 0.5D, pos.z, stack);
            entityitem.setDefaultPickupDelay();
            world.spawnEntity(entityitem);
        }
    }

    private void dropNetworkItems(AbsoluteNetworkState state) {
        for (MutableSortedIntMap.Entry<ItemStack> entry : state.getAttachments().entries()) {
            dropAttachment(state, entry.getValue(), entry.getKey());
        }
    }

    private Network createAndAddNetwork(AbsoluteNetworkState state) {
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
    protected NetworkEdge createNetworkEdge(INetwork network, Graph.Edge graphEdge) {
        return new NetworkEdge(network, graphEdge);
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
        RelativeNetworkState relativeState = RelativeNetworkState.fromAbsolute(network.getState());
        relativeState.addEdge(fromPos, toPos);
        network.setState(relativeState.toAbsolute());
    }

    @Override
    public final boolean connect(BlockPos fromPos, BlockPos toPos) {
        if (fromPos.equals(toPos)) {
            INetworkNode node = getNetworkNodeByPos(fromPos);
            if (node != null) {
                INetwork network = node.getNetwork();
                RelativeNetworkState relativeState = RelativeNetworkState.fromAbsolute(network.getState());
                relativeState.reroot(toPos);
                network.setState(relativeState.toAbsolute());
            }
            return false;
        }

        INetworkNode fromNode = getNetworkNodeByPos(fromPos);
        INetworkNode toNode = getNetworkNodeByPos(toPos);

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

                RelativeNetworkState fromState = RelativeNetworkState.fromAbsolute(fromNetwork.getState());
                RelativeNetworkState toState = RelativeNetworkState.fromAbsolute(toNetwork.getState());
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
                AbsoluteNetworkState state = AbsoluteNetworkState.createInitial(BasicTree.createInitial(fromPos, toPos).toAbsolute());
                createAndAddNetwork(state);
            }
        }

        return true;
    }

    private void applySplitResult(RelativeNetworkState.SplitResult splitResult) {
        for (RelativeNetworkState subState : splitResult.getSubStates()) {
            createAndAddNetwork(subState.toAbsolute());
        }
        dropNetworkItems(splitResult.getState().toAbsolute());
    }

    @Override
    public final void destroy(BlockPos pos) {
        INetworkNode node = getNetworkNodeByPos(pos);
        if (node != null) {
            INetwork network = node.getNetwork();
            RelativeNetworkState state = RelativeNetworkState.fromAbsolute(network.getState());
            state.reroot(pos);
            removeNetwork(network);
            applySplitResult(state.splitRoot());
        }
    }

    @Override
    public void disconnect(BlockPos posA, BlockPos posB) {
        INetworkNode nodeA = getNetworkNodeByPos(posA);
        INetworkNode nodeB = getNetworkNodeByPos(posB);
        if (nodeA != null && nodeB != null) {
            INetwork network = nodeA.getNetwork();
            if (network == nodeB.getNetwork()) {
                RelativeNetworkState state = RelativeNetworkState.fromAbsolute(network.getState());
                state.reroot(posA);
                removeNetwork(network);
                applySplitResult(state.splitEdge(posB));
            }
        }
    }
}
