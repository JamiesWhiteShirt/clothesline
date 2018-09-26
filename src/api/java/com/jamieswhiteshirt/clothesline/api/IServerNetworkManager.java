package com.jamieswhiteshirt.clothesline.api;

import java.util.List;

/**
 * Manages all the clothesline networks in a server world.
 */
public interface IServerNetworkManager extends INetworkManager<INetworkEdge, INetworkNode> {
    /**
     * Resets the collection of clothesline networks that are currently loaded.
     * @param networks collection of clothesline networks
     */
    void reset(List<PersistentNetwork> networks);
}
