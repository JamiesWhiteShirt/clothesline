package com.jamieswhiteshirt.clothesline.api;

import net.minecraft.util.math.BlockPos;

/**
 * Manages all the clothesline networks in a world.
 */
public interface INetworkManager {
    /**
     * Returns the collection of clothesline networks that are currently loaded.
     * @return the collection of clothesline networks that are currently loaded
     */
    INetworkCollection getNetworks();

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
     * create entirely new ones.
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
     * create entirely new ones.
     * @param posA the first node position
     * @param posB the second node position
     * @return true if there was a connection between the nodes, false otherwise
     */
    boolean disconnect(BlockPos posA, BlockPos posB);

    /**
     * Creates an empty clothesline network at the specified position
     * @param pos the specified position to create an empty clothesline network at
     */
    void createNode(BlockPos pos);

    /**
     * Removes the specified node from any clothesline network that is connected to it.
     *
     * This operation may modify the structure of existing clothesline networks, remove existing clothesline networks or
     * create entirely new ones.
     * @param pos
     */
    void destroyNode(BlockPos pos);
}
