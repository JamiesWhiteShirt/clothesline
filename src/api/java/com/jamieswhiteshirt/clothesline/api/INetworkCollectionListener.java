package com.jamieswhiteshirt.clothesline.api;

public interface INetworkCollectionListener {
    void onNetworkAdded(INetworkCollection networkMap, INetwork network);

    void onNetworkRemoved(INetworkCollection networkMap, INetwork network);
}
