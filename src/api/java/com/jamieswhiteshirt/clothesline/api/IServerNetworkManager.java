package com.jamieswhiteshirt.clothesline.api;

import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * Manages all the clothesline networks in a server world.
 */
public interface IServerNetworkManager extends INetworkManager<INetworkEdge, INetworkNode> {
    /**
     * Resets the collection of clothesline networks that are currently loaded.
     * @param networks collection of clothesline networks
     */
    void reset(List<PersistentNetwork> networks);

    /**
     * Get a clothesline network by its longer UUID, or null if no clothesline network with the UUID exists
     * @see INetwork#getUuid()
     * @param uuid the longer UUID
     * @return a clothesline network, or null if no clothesline network with the UUID exists
     */
    @Nullable
    INetwork getNetworkByUuid(UUID uuid);

    /**
     * Destroys the connection between two nodes if it exists.
     *
     * This operation may modify the structure of existing clothesline networks, remove existing clothesline networks or
     * create entirely new ones, in which case event listeners will be notified with
     * {@link INetworkManagerEventListener#onNetworkAdded(INetworkManager, INetwork)} and
     * {@link INetworkManagerEventListener#onNetworkRemoved(INetworkManager, INetwork)} respectively.
     * @param posA the first node position
     * @param posB the second node position
     */
    void disconnect(BlockPos posA, BlockPos posB);
}
