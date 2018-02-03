package com.jamieswhiteshirt.clothesline.common.impl;

import com.jamieswhiteshirt.clothesline.api.*;
import com.jamieswhiteshirt.rtree3i.ConfigurationBuilder;
import com.jamieswhiteshirt.rtree3i.RTree;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.*;

public abstract class CommonNetworkManager implements ICommonNetworkManager {
    private static final class NetworkNode implements INetworkNode {
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            NetworkNode that = (NetworkNode) o;
            return Objects.equals(network, that.network) &&
                Objects.equals(graphNode, that.graphNode);
        }

        @Override
        public int hashCode() {
            return Objects.hash(network, graphNode);
        }
    }

    private static final class NetworkEdge implements INetworkEdge {
        private final Network network;
        private final NetworkGraph.Edge graphEdge;

        private NetworkEdge(Network network, NetworkGraph.Edge graphEdge) {
            this.network = network;
            this.graphEdge = graphEdge;
        }

        @Override
        public Network getNetwork() {
            return network;
        }

        @Override
        public NetworkGraph.Edge getGraphEdge() {
            return graphEdge;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            NetworkEdge that = (NetworkEdge) o;
            return Objects.equals(network, that.network) &&
                Objects.equals(graphEdge, that.graphEdge);
        }

        @Override
        public int hashCode() {
            return Objects.hash(network, graphEdge);
        }
    }

    private List<Network> networks = new ArrayList<>();
    private IntHashMap<Network> networksById = new IntHashMap<>();
    private Map<BlockPos, NetworkNode> networkNodesByPos = new HashMap<>();
    private RTree<INetworkEdge> networkEdges = RTree.create(new ConfigurationBuilder().star().build());
    private final List<INetworkManagerEventListener> eventListeners = new ArrayList<>();

    private void unassignNetworkGraph(Network network) {
        NetworkGraph graph = network.getState().getGraph();
        for (NetworkGraph.Node graphNode : graph.getNodes()) {
            networkNodesByPos.remove(graphNode.getKey());
        }
        for (NetworkGraph.Edge graphEdge : graph.getAllEdges()) {
            networkEdges = networkEdges.remove(graphEdge.getBox(), new NetworkEdge(network, graphEdge));
        }
    }

    public CommonNetworkManager() { }

    protected void resetInternal(List<Network> networks) {
        this.networks = new ArrayList<>(networks);
        this.networksById = new IntHashMap<>();
        for (Network network : networks) {
            networksById.addKey(network.getId(), network);
        }
        this.networkNodesByPos = new HashMap<>();
        for (Network network : networks) {
            assignNetworkGraph(network);
        }

        for (INetworkManagerEventListener eventListener : eventListeners) {
            eventListener.onNetworksReset(getNetworks());
        }
    }

    @Override
    public List<Network> getNetworks() {
        return networks;
    }

    @Nullable
    @Override
    public Network getNetworkById(int id) {
        return networksById.lookup(id);
    }

    @Nullable
    @Override
    public final INetworkNode getNetworkNodeByPos(BlockPos pos) {
        return networkNodesByPos.get(pos);
    }

    @Override
    public RTree<INetworkEdge> getNetworkEdges() {
        return networkEdges;
    }

    private void assignNetworkGraph(Network network) {
        NetworkGraph graph = network.getState().getGraph();
        for (NetworkGraph.Node graphNode : graph.getNodes()) {
            networkNodesByPos.put(graphNode.getKey(), new NetworkNode(network, graphNode));
        }
        for (NetworkGraph.Edge graphEdge : graph.getAllEdges()) {
            networkEdges = networkEdges.add(graphEdge.getBox(), new NetworkEdge(network, graphEdge));
        }
    }

    protected void addNetwork(Network network) {
        networks.add(network);
        networksById.addKey(network.getId(), network);
        assignNetworkGraph(network);

        for (INetworkManagerEventListener eventListener : eventListeners) {
            eventListener.onNetworkAdded(network);
        }
    }

    @Override
    public void removeNetwork(Network network) {
        networks.remove(network);
        networksById.removeObject(network.getId());
        unassignNetworkGraph(network);

        for (INetworkManagerEventListener eventListener : eventListeners) {
            eventListener.onNetworkRemoved(network);
        }
    }

    @Override
    public final void update() {
        getNetworks().forEach(Network::update);
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
    public ItemStack insertItem(Network network, int attachmentKey, ItemStack stack, boolean simulate) {
        if (!stack.isEmpty() && network.getState().getAttachment(attachmentKey).isEmpty()) {
            if (!simulate) {
                ItemStack insertedItem = stack.copy();
                insertedItem.setCount(1);
                setAttachment(network, attachmentKey, insertedItem);
            }

            ItemStack returnedStack = stack.copy();
            returnedStack.shrink(1);
            return returnedStack;
        }
        return stack;
    }

    @Override
    public ItemStack extractItem(Network network, int attachmentKey, boolean simulate) {
        ItemStack result = network.getState().getAttachment(attachmentKey);
        if (!result.isEmpty() && !simulate) {
            setAttachment(network, attachmentKey, ItemStack.EMPTY);
        }
        return result;
    }

    @Override
    public void setAttachment(Network network, int attachmentKey, ItemStack stack) {
        ItemStack previousStack = network.getState().getAttachment(attachmentKey);
        network.getState().setAttachment(attachmentKey, stack);

        for (INetworkManagerEventListener eventListener : eventListeners) {
            eventListener.onAttachmentChanged(network, attachmentKey, previousStack, stack);
        }
    }

    @Override
    public void addMomentum(Network network, int momentum) {
        network.getState().addMomentum(momentum);
    }

    @Override
    public boolean useItem(Network network, EntityPlayer player, EnumHand hand, int attachmentKey) {
        ItemStack stack = player.getHeldItem(hand);
        if (!stack.isEmpty()) {
            if (network.getState().getAttachment(attachmentKey).isEmpty()) {
                player.setHeldItem(hand, insertItem(network, attachmentKey, stack, false));
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
