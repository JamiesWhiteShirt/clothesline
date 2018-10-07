package com.jamieswhiteshirt.clothesline.api;

import com.jamieswhiteshirt.rtree3i.RTreeMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface INetworkCollection {
    /**
     * Returns the collection of clothesline networks.
     * @return the collection of clothesline networks
     */
    List<INetwork> getValues();

    /**
     * Get a clothesline network by its shorter ID, or null if no clothesline network with the ID exists.
     * @see INetwork#getId()
     * @param id the shorter ID
     * @return a clothesline network, or null if no clothesline network with the ID exists
     */
    @Nullable
    INetwork getById(int id);

    /**
     * Get a clothesline network by its longer UUID, or null if no clothesline network with the UUID exists.
     * @see INetwork#getUuid()
     * @param uuid the longer UUID
     * @return a clothesline network, or null if no clothesline network with the UUID exists
     */
    @Nullable
    INetwork getByUuid(UUID uuid);

    /**
     * Adds the clothesline network to the collection of clothesline networks.
     *
     * Notifies event listeners with
     * {@link INetworkCollectionListener#onNetworkRemoved(INetworkCollection, INetwork)}.
     * @param network the clothesline network
     */
    void add(INetwork network);

    /**
     * Removes the clothesline network from the collection of clothesline networks.
     *
     * Notifies event listeners with
     * {@link INetworkCollectionListener#onNetworkRemoved(INetworkCollection, INetwork)}.
     * @param network the clothesline network
     */
    void remove(INetwork network);

    /**
     * Removes the clothesline network by its shorter ID from the collection of clothesline networks.
     *
     * Notifies event listeners with
     * {@link INetworkCollectionListener#onNetworkRemoved(INetworkCollection, INetwork)}.
     * @param id the shorter ID
     */
    void removeById(int id);

    /**
     * Removes the clothesline network by its longer UUID from the collection of clothesline networks.
     *
     * Notifies event listeners with
     * {@link INetworkCollectionListener#onNetworkRemoved(INetworkCollection, INetwork)}.
     * @param uuid the longer UUID
     */
    void removeByUuid(UUID uuid);

    /**
     * Returns a spatial index of all the path edges of all currently loaded networks.
     * @return a spatial index of all the path edges of all currently loaded networks
     */
    RTreeMap<Line, INetworkEdge> getEdges();

    /**
     * Returns a spatial index of all the path nodes of all currently loaded networks.
     * @return a spatial index of all the path nodes of all currently loaded networks
     */
    RTreeMap<BlockPos, INetworkNode> getNodes();

    /**
     * Returns a set of networks spanning the chunk at the specified position. This set must not be modified
     * @return a set of networks spanning the chunk at the specified position
     */
    Set<INetwork> getNetworksSpanningChunk(int x, int z);

    /**
     * Adds an event listener that will be notified when the collection of clothesline networks changes. The event
     * listener is bound by a key which must be unique for the clothesline network. If an existing event listener is
     * bound to the same key, it will be overridden.
     *
     * The event listener can be removed with {@link #removeEventListener(ResourceLocation)}.
     * @param key the event listener key
     * @param eventListener the event listener
     */
    void addEventListener(ResourceLocation key, INetworkCollectionListener eventListener);

    /**
     * Removes an event listener bound to the specified key with
     * {@link #addEventListener(ResourceLocation, INetworkCollectionListener)}. If no event listener is bound to the
     * key, nothing happens.
     * @param key the event listener key
     */
    void removeEventListener(ResourceLocation key);
}
