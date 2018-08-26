package com.jamieswhiteshirt.clothesline.api;

import com.jamieswhiteshirt.rtree3i.RTreeMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * Manages all the clothesline networks in a world.
 * @param <E> the type of network edge
 * @param <N> the type of network node
 */
public interface INetworkManager<E extends INetworkEdge, N extends INetworkNode> {
    /**
     * Returns the collection of clothesline networks that are currently loaded.
     * @return the collection of clothesline networks that are currently loaded
     */
    Collection<INetwork> getNetworks();

    /**
     * Get a clothesline network by its shorter ID, or null if no clothesline network with the ID exists
     * @see INetwork#getId()
     * @param id the shorter ID
     * @return a clothesline network, or null if no clothesline network with the ID exists
     */
    @Nullable
    INetwork getNetworkById(int id);

    /**
     * Returns a spatial index of all the path edges of all currently loaded networks.
     * @return a spatial index of all the path edges of all currently loaded networks
     */
    RTreeMap<Line, E> getEdges();

    /**
     * Returns a spatial index of all the path nodes of all currently loaded networks.
     * @return a spatial index of all the path nodes of all currently loaded networks
     */
    RTreeMap<BlockPos, N> getNodes();

    /**
     * Removes the clothesline network from the clothesline network manager.
     *
     * Notifies event listeners with
     * {@link INetworkManagerEventListener#onNetworkRemoved(INetworkManager, INetwork)}.
     * @param network the clothesline network
     */
    void removeNetwork(INetwork network);

    /**
     * Updates the state of all loaded clothesline networks, called each in-game tick.
     */
    void update();

    /**
     * Attempts to connect two nodes into the same clothesline network. Returns true if the nodes exist and they are not
     * already connected, false otherwise.
     *
     * This operation may not modify anything if the world is a client world.
     *
     * This operation may modify the structure of existing clothesline networks, remove existing clothesline networks or
     * create entirely new ones, in which case event listeners will be notified with
     * {@link INetworkManagerEventListener#onNetworkAdded(INetworkManager, INetwork)} and
     * {@link INetworkManagerEventListener#onNetworkRemoved(INetworkManager, INetwork)} respectively.
     * @param fromPos the first node position
     * @param toPos the second node position
     * @return true if the nodes exist and they are not already connected, false otherwise
     */
    boolean connect(BlockPos fromPos, BlockPos toPos);

    /**
     * Attempts to remove the connection between two nodes. Returns true if there was a connection between the nodes,
     * false otherwise. If it results in removal of items, items will be spawned in the world.
     *
     * This operation may not modify anything if the world is a client world.
     *
     * This operation may modify the structure of existing clothesline networks, remove existing clothesline networks or
     * create entirely new ones, in which case event listeners will be notified with
     * {@link INetworkManagerEventListener#onNetworkAdded(INetworkManager, INetwork)} and
     * {@link INetworkManagerEventListener#onNetworkRemoved(INetworkManager, INetwork)} respectively.
     * @param posA the first node position
     * @param posB the second node position
     * @return true if there was a connection between the nodes, false otherwise
     */
    boolean disconnect(BlockPos posA, BlockPos posB);

    /**
     * Creates an empty clothesline network at the specified position
     *
     * Notifies event listeners with
     * {@link INetworkManagerEventListener#onNetworkAdded(INetworkManager, INetwork)}.
     * @param pos the specified position to create an empty clothesline network at
     */
    void createNode(BlockPos pos);

    /**
     * Removes the specified node from any clothesline network that is connected to it.
     *
     * This operation may modify the structure of existing clothesline networks, remove existing clothesline networks or
     * create entirely new ones, in which case event listeners will be notified with
     * {@link INetworkManagerEventListener#onNetworkAdded(INetworkManager, INetwork)} and
     * {@link INetworkManagerEventListener#onNetworkRemoved(INetworkManager, INetwork)} respectively.
     * @param pos
     */
    void destroyNode(BlockPos pos);

    /**
     * Adds an event listener that will be notified when the collection of clothesline networks changes. The event
     * listener is bound by a key which must be unique for the clothesline network. If an existing event listener is
     * bound to the same key, it will be overridden.
     *
     * The event listener can be removed with {@link #removeEventListener(ResourceLocation)}.
     * @param key the event listener key
     * @param eventListener the event listener
     */
    void addEventListener(ResourceLocation key, INetworkManagerEventListener<E, N> eventListener);

    /**
     * Removes an event listener bound to the specified key with
     * {@link #addEventListener(ResourceLocation, INetworkManagerEventListener)}. If no event listener is bound to the
     * key, nothing happens.
     * @param key the event listener key
     */
    void removeEventListener(ResourceLocation key);
}
