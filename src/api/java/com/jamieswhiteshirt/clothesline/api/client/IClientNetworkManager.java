package com.jamieswhiteshirt.clothesline.api.client;

import com.jamieswhiteshirt.clothesline.api.INetwork;
import com.jamieswhiteshirt.clothesline.api.INetworkManager;
import com.jamieswhiteshirt.clothesline.api.INetworkNode;

import java.util.List;

/**
 * Manages all the clothesline networks in a client world.
 */
public interface IClientNetworkManager extends INetworkManager<IClientNetworkEdge, INetworkNode> {
    /**
     * Adds the clothesline network to the collection of clothesline networks that are currently loaded
     * @param network
     */
    void addNetwork(INetwork network);

    /**
     * Resets the collection of clothesline networks that are currently loaded.
     * @param networks
     */
    void reset(List<INetwork> networks);
}
