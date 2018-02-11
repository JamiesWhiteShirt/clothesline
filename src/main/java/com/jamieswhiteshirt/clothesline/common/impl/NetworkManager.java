package com.jamieswhiteshirt.clothesline.common.impl;

import com.jamieswhiteshirt.clothesline.api.*;
import com.jamieswhiteshirt.rtree3i.ConfigurationBuilder;
import com.jamieswhiteshirt.rtree3i.RTree;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.*;

public abstract class NetworkManager<T extends INetworkEdge> implements INetworkManager<T> {
    private List<INetwork> networks = new ArrayList<>();
    private IntHashMap<INetwork> networksById = new IntHashMap<>();
    private Map<BlockPos, INetworkNode> networkNodesByPos = new HashMap<>();
    private RTree<T> networkEdges = RTree.create(new ConfigurationBuilder().star().build());
    private final Map<ResourceLocation, INetworkManagerEventListener> eventListeners = new TreeMap<>();

    private void unassignNetworkGraph(INetwork network, Graph graph) {
        for (Graph.Node graphNode : graph.getNodes()) {
            networkNodesByPos.remove(graphNode.getKey(), new NetworkNode(network, graphNode));
        }
        for (Graph.Edge graphEdge : graph.getEdges()) {
            networkEdges = networkEdges.remove(graphEdge.getBox(), createNetworkEdge(network, graphEdge));
        }
    }


    private void assignNetworkGraph(INetwork network, Graph graph) {
        for (Graph.Node graphNode : graph.getNodes()) {
            networkNodesByPos.put(graphNode.getKey(), new NetworkNode(network, graphNode));
        }
        for (Graph.Edge graphEdge : graph.getEdges()) {
            networkEdges = networkEdges.add(graphEdge.getBox(), createNetworkEdge(network, graphEdge));
        }
    }

    private static final ResourceLocation SPATIAL_INDEX_KEY = new ResourceLocation("clothesline", "spatial_index");

    private void startIndexing(INetwork network) {
        assignNetworkGraph(network, network.getState().getGraph());
        network.addEventListener(SPATIAL_INDEX_KEY, new INetworkEventListener() {
            @Override
            public void onStateChanged(AbsoluteNetworkState previousState, AbsoluteNetworkState newState) {
                unassignNetworkGraph(network, previousState.getGraph());
                assignNetworkGraph(network, newState.getGraph());
            }

            @Override
            public void onAttachmentChanged(int attachmentKey, ItemStack previousStack, ItemStack newStack) { }
        });
    }

    private void stopIndexing(INetwork network) {
        unassignNetworkGraph(network, network.getState().getGraph());
        network.removeEventListener(SPATIAL_INDEX_KEY);
    }

    protected abstract T createNetworkEdge(INetwork network, Graph.Edge graphEdge);

    protected NetworkManager() { }

    protected void resetInternal(List<INetwork> networks) {
        List<INetwork> previousNetworks = this.networks;
        this.networks = new ArrayList<>(networks);
        this.networksById = new IntHashMap<>();
        for (INetwork network : networks) {
            networksById.addKey(network.getId(), network);
        }
        this.networkNodesByPos = new HashMap<>();
        this.networkEdges = RTree.create(new ConfigurationBuilder().star().build());
        for (INetwork network : networks) {
            startIndexing(network);
        }

        for (INetworkManagerEventListener eventListener : eventListeners.values()) {
            eventListener.onNetworksReset(previousNetworks, this.networks);
        }
    }

    @Override
    public List<INetwork> getNetworks() {
        return networks;
    }

    @Nullable
    @Override
    public INetwork getNetworkById(int id) {
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

    protected void addNetwork(INetwork network) {
        networks.add(network);
        networksById.addKey(network.getId(), network);
        startIndexing(network);

        for (INetworkManagerEventListener eventListener : eventListeners.values()) {
            eventListener.onNetworkAdded(network);
        }
    }

    @Override
    public void removeNetwork(INetwork network) {
        networks.remove(network);
        networksById.removeObject(network.getId());
        stopIndexing(network);

        for (INetworkManagerEventListener eventListener : eventListeners.values()) {
            eventListener.onNetworkRemoved(network);
        }
    }

    @Override
    public final void update() {
        getNetworks().forEach(INetwork::update);
    }

    @Override
    public void addEventListener(ResourceLocation key, INetworkManagerEventListener eventListener) {
        eventListeners.put(key, eventListener);
    }

    @Override
    public void removeEventListener(ResourceLocation key) {
        eventListeners.remove(key);
    }
}
