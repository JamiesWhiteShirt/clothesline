package com.jamieswhiteshirt.clothesline.api;

import java.util.List;

public interface INetworkManagerEventListener<T extends INetworkEdge> {
    void onNetworksReset(INetworkManager<T> networkManager, List<INetwork> previousNetworks, List<INetwork> newNetworks);

    void onNetworkAdded(INetworkManager<T> networkManager, INetwork network);

    void onNetworkRemoved(INetworkManager<T> networkManager, INetwork network);
}
