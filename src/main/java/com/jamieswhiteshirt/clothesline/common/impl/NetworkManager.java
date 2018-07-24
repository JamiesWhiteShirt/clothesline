package com.jamieswhiteshirt.clothesline.common.impl;

import com.jamieswhiteshirt.clothesline.api.*;
import com.jamieswhiteshirt.rtree3i.Box;
import com.jamieswhiteshirt.rtree3i.Configuration;
import com.jamieswhiteshirt.rtree3i.ConfigurationBuilder;
import com.jamieswhiteshirt.rtree3i.RTreeMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.*;

public abstract class NetworkManager<E extends INetworkEdge, N extends INetworkNode> implements INetworkManager<E, N> {
    private static final Configuration configuration = new ConfigurationBuilder().star().build();

    private static <E> RTreeMap<Line, E> createEdgesMap() {
        return RTreeMap.create(configuration, Line::getBox);
    }

    private static <N> RTreeMap<BlockPos, N> createNodesMap() {
        return RTreeMap.create(configuration, blockPos -> Box.create(blockPos.getX(), blockPos.getY(), blockPos.getZ(),
            blockPos.getX() + 1, blockPos.getY() + 1, blockPos.getZ() + 1));
    }

    private final World world;
    private List<INetwork> networks = new ArrayList<>();
    private IntHashMap<INetwork> networksById = new IntHashMap<>();
    private RTreeMap<Line, E> networkEdges = createEdgesMap();
    private RTreeMap<BlockPos, N> networkNodes = createNodesMap();
    private final Map<ResourceLocation, INetworkManagerEventListener<E, N>> eventListeners = new TreeMap<>();
    private final INetworkEventListener spatialIndexListener = new INetworkEventListener() {
        @Override
        public void onStateChanged(INetwork network, INetworkState previousState, INetworkState newState) {
            unassignNetworkGraph(network, previousState.getGraph());
            assignNetworkGraph(network, newState.getGraph());
        }

        @Override
        public void onAttachmentChanged(INetwork network, int attachmentKey, ItemStack previousStack, ItemStack newStack) { }
    };

    private void unassignNetworkGraph(INetwork network, Graph graph) {
        for (BlockPos pos : graph.getNodes().keySet()) {
            networkNodes = networkNodes.remove(pos);
        }
        for (Graph.Edge graphEdge : graph.getEdges()) {
            networkEdges = networkEdges.remove(graphEdge.getLine());
        }
    }


    private void assignNetworkGraph(INetwork network, Graph graph) {
        for (Graph.Node graphNode : graph.getNodes().values()) {
            networkNodes = networkNodes.put(graphNode.getKey(), createNetworkNode(graphNode, network));
        }
        int i = 0;
        for (Graph.Edge graphEdge : graph.getEdges()) {
            networkEdges = networkEdges.put(graphEdge.getLine(), createNetworkEdge(graphEdge, network, i++));
        }
    }

    private static final ResourceLocation SPATIAL_INDEX_KEY = new ResourceLocation("clothesline", "spatial_index");

    private void startIndexing(INetwork network) {
        assignNetworkGraph(network, network.getState().getGraph());
        network.addEventListener(SPATIAL_INDEX_KEY, spatialIndexListener);
    }

    private void stopIndexing(INetwork network) {
        unassignNetworkGraph(network, network.getState().getGraph());
        network.removeEventListener(SPATIAL_INDEX_KEY);
    }

    protected abstract E createNetworkEdge(Graph.Edge graphEdge, INetwork network, int index);

    protected abstract N createNetworkNode(Graph.Node graphNode, INetwork network);

    protected NetworkManager(World world) {
        this.world = world;
    }

    protected void resetInternal(List<INetwork> networks) {
        List<INetwork> previousNetworks = this.networks;
        this.networks = new ArrayList<>(networks);
        this.networksById = new IntHashMap<>();
        for (INetwork network : networks) {
            networksById.addKey(network.getId(), network);
        }
        this.networkNodes = createNodesMap();
        this.networkEdges = createEdgesMap();
        for (INetwork network : networks) {
            startIndexing(network);
        }

        for (INetworkManagerEventListener<E, N> eventListener : eventListeners.values()) {
            eventListener.onNetworksReset(this, previousNetworks, this.networks);
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

    @Override
    public RTreeMap<BlockPos, N> getNodes() {
        return networkNodes;
    }

    @Override
    public RTreeMap<Line, E> getEdges() {
        return networkEdges;
    }

    protected void addNetwork(INetwork network) {
        networks.add(network);
        networksById.addKey(network.getId(), network);
        startIndexing(network);

        for (INetworkManagerEventListener<E, N> eventListener : eventListeners.values()) {
            eventListener.onNetworkAdded(this, network);
        }
    }

    @Override
    public void removeNetwork(INetwork network) {
        networks.remove(network);
        networksById.removeObject(network.getId());
        stopIndexing(network);

        for (INetworkManagerEventListener<E, N> eventListener : eventListeners.values()) {
            eventListener.onNetworkRemoved(this, network);
        }
    }

    @Override
    public final void update() {
        getNetworks().forEach(INetwork::update);
    }

    @Override
    public void addEventListener(ResourceLocation key, INetworkManagerEventListener<E, N> eventListener) {
        eventListeners.put(key, eventListener);
    }

    @Override
    public void removeEventListener(ResourceLocation key) {
        eventListeners.remove(key);
    }
}
