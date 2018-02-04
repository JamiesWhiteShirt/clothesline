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

public abstract class NetworkManager<T extends INetworkEdge> implements INetworkManager<T> {
    private List<Network> networks = new ArrayList<>();
    private IntHashMap<Network> networksById = new IntHashMap<>();
    private Map<BlockPos, INetworkNode> networkNodesByPos = new HashMap<>();
    private RTree<T> networkEdges = RTree.create(new ConfigurationBuilder().star().build());
    private final List<INetworkManagerEventListener> eventListeners = new ArrayList<>();

    private void unassignNetworkGraph(Network network) {
        Graph graph = network.getState().getGraph();
        for (Graph.Node graphNode : graph.getNodes()) {
            networkNodesByPos.remove(graphNode.getKey(), new NetworkNode(network, graphNode));
        }
        for (Graph.Edge graphEdge : graph.getEdges()) {
            networkEdges = networkEdges.remove(graphEdge.getBox(), createNetworkEdge(network, graphEdge));
        }
    }


    private void assignNetworkGraph(Network network) {
        Graph graph = network.getState().getGraph();
        for (Graph.Node graphNode : graph.getNodes()) {
            networkNodesByPos.put(graphNode.getKey(), new NetworkNode(network, graphNode));
        }
        for (Graph.Edge graphEdge : graph.getEdges()) {
            networkEdges = networkEdges.add(graphEdge.getBox(), createNetworkEdge(network, graphEdge));
        }
    }

    protected abstract T createNetworkEdge(Network network, Graph.Edge graphEdge);

    protected NetworkManager() { }

    protected void resetInternal(List<Network> networks) {
        this.networks = new ArrayList<>(networks);
        this.networksById = new IntHashMap<>();
        for (Network network : networks) {
            networksById.addKey(network.getId(), network);
        }
        this.networkNodesByPos = new HashMap<>();
        this.networkEdges = RTree.create(new ConfigurationBuilder().star().build());
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
    public RTree<T> getNetworkEdges() {
        return networkEdges;
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
