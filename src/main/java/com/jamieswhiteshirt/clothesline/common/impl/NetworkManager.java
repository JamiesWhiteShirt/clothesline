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
    private Map<UUID, INetwork> networksByUuid = new HashMap<>();
    private RTreeMap<Line, E> networkEdges = createEdgesMap();
    private RTreeMap<BlockPos, N> networkNodes = createNodesMap();
    private final Map<ResourceLocation, INetworkManagerEventListener<E, N>> eventListeners = new TreeMap<>();
    private final INetworkEventListener spatialIndexListener = new INetworkEventListener() {
        @Override
        public void onStateChanged(INetwork network, INetworkState previousState, INetworkState newState) {
            unassignNetworkPath(network, previousState.getPath());
            assignNetworkPath(network, newState.getPath());
        }

        @Override
        public void onAttachmentChanged(INetwork network, int attachmentKey, ItemStack previousStack, ItemStack newStack) { }
    };

    private void unassignNetworkPath(INetwork network, Path path) {
        for (BlockPos pos : path.getNodes().keySet()) {
            networkNodes = networkNodes.remove(pos);
        }
        for (Path.Edge pathEdge : path.getEdges()) {
            networkEdges = networkEdges.remove(pathEdge.getLine());
        }
    }


    private void assignNetworkPath(INetwork network, Path path) {
        for (Path.Node graphNode : path.getNodes().values()) {
            networkNodes = networkNodes.put(graphNode.getPos(), createNetworkNode(graphNode, network));
        }
        int i = 0;
        for (Path.Edge graphEdge : path.getEdges()) {
            networkEdges = networkEdges.put(graphEdge.getLine(), createNetworkEdge(graphEdge, network, i++));
        }
    }

    private static final ResourceLocation SPATIAL_INDEX_KEY = new ResourceLocation("clothesline", "spatial_index");

    private void startIndexing(INetwork network) {
        assignNetworkPath(network, network.getState().getPath());
        network.addEventListener(SPATIAL_INDEX_KEY, spatialIndexListener);
    }

    private void stopIndexing(INetwork network) {
        unassignNetworkPath(network, network.getState().getPath());
        network.removeEventListener(SPATIAL_INDEX_KEY);
    }

    protected abstract E createNetworkEdge(Path.Edge pathEdge, INetwork network, int index);

    protected abstract N createNetworkNode(Path.Node pathNode, INetwork network);

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
        this.networksByUuid = new HashMap<>();
        for (INetwork network : networks) {
            networksByUuid.put(network.getUuid(), network);
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

    @Nullable
    @Override
    public final INetwork getNetworkByUuid(UUID uuid) {
        return networksByUuid.get(uuid);
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
        networksByUuid.put(network.getUuid(), network);
        startIndexing(network);

        for (INetworkManagerEventListener<E, N> eventListener : eventListeners.values()) {
            eventListener.onNetworkAdded(this, network);
        }
    }

    @Override
    public void removeNetwork(INetwork network) {
        networks.remove(network);
        networksById.removeObject(network.getId());
        networksByUuid.remove(network.getUuid());
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
