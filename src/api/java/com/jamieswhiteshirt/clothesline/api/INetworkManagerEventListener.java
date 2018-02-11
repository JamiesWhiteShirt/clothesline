package com.jamieswhiteshirt.clothesline.api;

import java.util.List;

public interface INetworkManagerEventListener {
    void onNetworksReset(List<INetwork> previousNetworks, List<INetwork> newNetworks);

    void onNetworkAdded(INetwork network);

    void onNetworkRemoved(INetwork network);
}
