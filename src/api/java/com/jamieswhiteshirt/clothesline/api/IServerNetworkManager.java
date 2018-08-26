package com.jamieswhiteshirt.clothesline.api;

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
}
