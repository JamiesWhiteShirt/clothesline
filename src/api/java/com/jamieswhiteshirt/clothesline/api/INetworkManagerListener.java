package com.jamieswhiteshirt.clothesline.api;

import java.util.List;

public interface INetworkManagerListener<E extends INetworkEdge, N extends INetworkNode> {
    void onNetworksReset(INetworkManager<E, N> networkManager, List<INetwork> previousNetworks, List<INetwork> newNetworks);

    void onNetworkAdded(INetworkManager<E, N> networkManager, INetwork network);

    void onNetworkRemoved(INetworkManager<E, N> networkManager, INetwork network);
}
