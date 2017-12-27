package com.jamieswhiteshirt.clothesline.common.impl;

import com.jamieswhiteshirt.clothesline.api.*;
import com.jamieswhiteshirt.clothesline.common.util.BasicTree;
import com.jamieswhiteshirt.clothesline.common.util.RelativeNetworkState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public final class NetworkManager implements INetworkManager {
    private static class NetworkNode implements INetworkNode {
        private final Network network;
        private final NetworkGraph.Node graphNode;

        private NetworkNode(Network network, NetworkGraph.Node graphNode) {
            this.network = network;
            this.graphNode = graphNode;
        }

        @Override
        public Network getNetwork() {
            return network;
        }

        @Override
        public NetworkGraph.Node getGraphNode() {
            return graphNode;
        }
    }

    private final List<INetworkManagerEventListener> eventListeners = new ArrayList<>();
    private HashMap<UUID, Network> networksByUuid = new HashMap<>();
    private HashMap<BlockPos, NetworkNode> networkNodesByPos = new HashMap<>();

    @Override
    public final Collection<Network> getNetworks() {
        return networksByUuid.values();
    }

    @Nullable
    @Override
    public final Network getNetworkByUUID(UUID uuid) {
        return networksByUuid.get(uuid);
    }

    @Nullable
    @Override
    public final INetworkNode getNetworkNodeByPos(BlockPos pos) {
        return networkNodesByPos.get(pos);
    }

    @Override
    public void addNetwork(Network network) {
        if (network != networksByUuid.put(network.getUuid(), network)) {
            assignNetworkGraph(network);

            for (INetworkManagerEventListener eventListener : eventListeners) {
                eventListener.onNetworkAdded(network);
            }
        }
    }

    private void assignNetworkGraph(Network network) {
        for (NetworkGraph.Node graphNode : network.getState().getGraph().getNodes()) {
            networkNodesByPos.put(graphNode.getKey(), new NetworkNode(network, graphNode));
        }
    }

    @Override
    public void removeNetwork(UUID networkUuid) {
        Network network = networksByUuid.remove(networkUuid);
        if (network != null) {
            unassignNetworkGraph(network);

            for (INetworkManagerEventListener eventListener : eventListeners) {
                eventListener.onNetworkRemoved(network);
            }
        }
    }

    private void unassignNetworkGraph(Network network) {
        for (NetworkGraph.Node graphNode : network.getState().getGraph().getNodes()) {
            networkNodesByPos.remove(graphNode.getKey());
        }
    }

    @Override
    public void setNetworks(Collection<Network> networks) {
        this.networksByUuid = new HashMap<>(networks.stream().collect(Collectors.toMap(
                Network::getUuid,
                network -> network
        )));
        this.networkNodesByPos = new HashMap<>();
        for (Network network : networks) {
            assignNetworkGraph(network);
        }

        for (INetworkManagerEventListener eventListener : eventListeners) {
            eventListener.onNetworksSet(getNetworks());
        }
    }

    @Override
    public final void update() {
        networksByUuid.values().forEach(Network::update);
    }

    private void extend(Network network, BlockPos fromPos, BlockPos toPos) {
        RelativeNetworkState relativeState = RelativeNetworkState.fromAbsolute(network.getState());
        relativeState.addEdge(fromPos, toPos);
        setNetworkState(network, relativeState.toAbsolute());
    }

    @Override
    public final boolean connect(BlockPos posA, BlockPos posB) {
        if (posA.equals(posB)) {
            INetworkNode node = getNetworkNodeByPos(posA);
            if (node != null) {
                Network network = node.getNetwork();
                RelativeNetworkState relativeState = RelativeNetworkState.fromAbsolute(network.getState());
                relativeState.reroot(posB);
                setNetworkState(network, relativeState.toAbsolute());
                return true;
            }
            return false;
        }

        INetworkNode nodeA = getNetworkNodeByPos(posA);
        INetworkNode nodeB = getNetworkNodeByPos(posB);

        if (nodeA != null) {
            Network networkA = nodeA.getNetwork();
            if (nodeB != null) {
                Network networkB = nodeB.getNetwork();

                if (networkA == networkB) {
                    //TODO: Look into circular networks
                    return false;
                }

                removeNetwork(networkA.getUuid());
                removeNetwork(networkB.getUuid());

                RelativeNetworkState stateA = RelativeNetworkState.fromAbsolute(networkA.getState());
                RelativeNetworkState stateB = RelativeNetworkState.fromAbsolute(networkB.getState());
                stateB.reroot(posB);
                stateA.addSubState(posA, stateB);
                Network network = new Network(UUID.randomUUID(), stateA.toAbsolute());

                addNetwork(network);
            } else {
                extend(networkA, posA, posB);
            }
        } else {
            if (nodeB != null) {
                Network networkB = nodeB.getNetwork();
                extend(networkB, posB, posA);
            } else {
                AbsoluteNetworkState state = AbsoluteNetworkState.createInitial(BasicTree.createInitial(posA, posB).toAbsolute());
                Network network = new Network(UUID.randomUUID(), state);
                network.getState().setAttachment(0, new ItemStack(Items.LEATHER_CHESTPLATE));

                addNetwork(network);
            }
        }

        return true;
    }

    @Override
    public final void destroy(BlockPos pos) {
        INetworkNode node = getNetworkNodeByPos(pos);
        if (node != null) {
            Network network = node.getNetwork();
            RelativeNetworkState state = RelativeNetworkState.fromAbsolute(network.getState());
            state.reroot(pos);
            RelativeNetworkState.SplitResult splitResult = state.splitRoot();
            removeNetwork(network.getUuid());
            for (RelativeNetworkState subState : splitResult.getSubStates()) {
                Network newNetwork = new Network(UUID.randomUUID(), subState.toAbsolute());
                addNetwork(newNetwork);
            }
        }
    }

    @Override
    public void disconnect(BlockPos posA, BlockPos posB) {
        INetworkNode nodeA = getNetworkNodeByPos(posA);
        INetworkNode nodeB = getNetworkNodeByPos(posB);
        if (nodeA != null && nodeB != null) {
            Network network = nodeA.getNetwork();
            if (network == nodeB.getNetwork()) {
                RelativeNetworkState state = RelativeNetworkState.fromAbsolute(network.getState());
                state.reroot(posA);
                RelativeNetworkState.SplitResult splitResult = state.splitEdge(posB);
                removeNetwork(network.getUuid());
                for (RelativeNetworkState subState : splitResult.getSubStates()) {
                    Network newNetwork = new Network(UUID.randomUUID(), subState.toAbsolute());
                    addNetwork(newNetwork);
                }
            }
        }
    }

    @Override
    public void setNetworkState(Network network, AbsoluteNetworkState state) {
        unassignNetworkGraph(network);
        network.setState(state);
        assignNetworkGraph(network);

        for (INetworkManagerEventListener eventListener : eventListeners) {
            eventListener.onNetworkStateChanged(network, state);
        }
    }

    @Override
    public ItemStack insertItem(Network network, int offset, ItemStack stack, boolean simulate) {
        if (!stack.isEmpty() && network.getState().getAttachment(offset).isEmpty()) {
            if (!simulate) {
                ItemStack insertedItem = stack.copy();
                insertedItem.setCount(1);
                setAttachment(network, offset, stack);
            }

            ItemStack returnedStack = stack.copy();
            returnedStack.shrink(1);
            return returnedStack;
        }
        return stack;
    }

    @Override
    public ItemStack extractItem(Network network, int offset, boolean simulate) {
        ItemStack result = network.getState().getAttachment(offset);
        if (!result.isEmpty() && !simulate) {
            setAttachment(network, offset, ItemStack.EMPTY);
        }
        return result;
    }

    @Override
    public void setAttachment(Network network, int offset, ItemStack stack) {
        ItemStack previousStack = network.getState().getAttachment(offset);
        network.getState().setAttachment(offset, stack);

        for (INetworkManagerEventListener eventListener : eventListeners) {
            eventListener.onAttachmentChanged(network, offset, previousStack, stack);
        }
    }

    @Override
    public void addMomentum(Network network, int momentum) {
        network.getState().addMomentum(momentum);
    }

    @Override
    public boolean useItem(Network network, EntityPlayer player, EnumHand hand, int offset) {
        ItemStack stack = player.getHeldItem(hand);
        if (!stack.isEmpty()) {
            if (network.getState().getAttachment(offset).isEmpty()) {
                player.setHeldItem(hand, insertItem(network, offset, stack, false));
                return true;
            }
        }
        return false;
    }

    @Override
    public void addEventListener(INetworkManagerEventListener eventListener) {
        eventListeners.add(eventListener);
    }

    @Override
    public void removeEventListener(INetworkManagerEventListener eventListener) {
        eventListeners.remove(eventListener);
    }
}
