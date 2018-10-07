package com.jamieswhiteshirt.clothesline.common.impl;

import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;
import com.jamieswhiteshirt.clothesline.api.*;
import com.jamieswhiteshirt.rtree3i.Box;
import com.jamieswhiteshirt.rtree3i.Configuration;
import com.jamieswhiteshirt.rtree3i.ConfigurationBuilder;
import com.jamieswhiteshirt.rtree3i.RTreeMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import javax.annotation.Nullable;
import java.util.*;

public final class NetworkCollection implements INetworkCollection {
    private static final Configuration configuration = new ConfigurationBuilder().star().build();

    private static <E> RTreeMap<Line, E> createEdgesMap() {
        return RTreeMap.create(configuration, Line::getBox);
    }

    private static <N> RTreeMap<BlockPos, N> createNodesMap() {
        return RTreeMap.create(configuration, blockPos -> Box.create(blockPos.getX(), blockPos.getY(), blockPos.getZ(),
            blockPos.getX() + 1, blockPos.getY() + 1, blockPos.getZ() + 1));
    }

    private final List<INetwork> values = new ArrayList<>();
    private final Int2ObjectMap<INetwork> byId = new Int2ObjectOpenHashMap<>();
    private final Map<UUID, INetwork> byUuid = new HashMap<>();
    private RTreeMap<Line, INetworkEdge> edges = createEdgesMap();
    private RTreeMap<BlockPos, INetworkNode> nodes = createNodesMap();
    private final SetMultimap<Long, INetwork> chunkSpanMap = MultimapBuilder.hashKeys().linkedHashSetValues().build();
    private final Map<ResourceLocation, INetworkCollectionListener> eventListeners = new TreeMap<>();

    @Override
    public List<INetwork> getValues() {
        return values;
    }

    @Nullable
    @Override
    public INetwork getById(int id) {
        return byId.get(id);
    }

    @Nullable
    @Override
    public INetwork getByUuid(UUID uuid) {
        return byUuid.get(uuid);
    }

    @Override
    public void add(INetwork network) {
        values.add(network);
        byId.put(network.getId(), network);
        byUuid.put(network.getUuid(), network);

        for (Path.Node pathNode : network.getState().getPath().getNodes().values()) {
            nodes = nodes.put(pathNode.getPos(), new NetworkNode(network, pathNode));
        }
        int i = 0;
        for (Path.Edge pathEdge : network.getState().getPath().getEdges()) {
            edges = edges.put(pathEdge.getLine(), new NetworkEdge(network, pathEdge, i++));
        }

        for (long position : network.getState().getChunkSpan()) {
            chunkSpanMap.put(position, network);
        }

        for (INetworkCollectionListener eventListener : eventListeners.values()) {
            eventListener.onNetworkAdded(this, network);
        }
    }

    @Override
    public void remove(INetwork network) {
        values.remove(network);
        byId.remove(network.getId());
        byUuid.remove(network.getUuid());

        for (BlockPos pos : network.getState().getPath().getNodes().keySet()) {
            nodes = nodes.remove(pos);
        }
        for (Path.Edge pathEdge : network.getState().getPath().getEdges()) {
            edges = edges.remove(pathEdge.getLine());
        }

        for (long position : network.getState().getChunkSpan()) {
            chunkSpanMap.remove(position, network);
        }

        for (INetworkCollectionListener eventListener : eventListeners.values()) {
            eventListener.onNetworkRemoved(this, network);
        }
    }

    @Override
    public void removeById(int id) {
        INetwork network = getById(id);
        if (network != null) remove(network);
    }

    @Override
    public void removeByUuid(UUID uuid) {
        INetwork network = getByUuid(uuid);
        if (network != null) remove(network);
    }

    @Override
    public RTreeMap<BlockPos, INetworkNode> getNodes() {
        return nodes;
    }

    @Override
    public RTreeMap<Line, INetworkEdge> getEdges() {
        return edges;
    }

    @Override
    public Set<INetwork> getNetworksSpanningChunk(int x, int z) {
        return chunkSpanMap.get(ChunkPos.asLong(x, z));
    }

    @Override
    public void addEventListener(ResourceLocation key, INetworkCollectionListener eventListener) {
        eventListeners.put(key, eventListener);
    }

    @Override
    public void removeEventListener(ResourceLocation key) {
        eventListeners.remove(key);
    }
}
