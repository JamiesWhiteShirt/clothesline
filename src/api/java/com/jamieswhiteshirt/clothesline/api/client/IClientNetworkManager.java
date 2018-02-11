package com.jamieswhiteshirt.clothesline.api.client;

import com.jamieswhiteshirt.clothesline.api.INetwork;
import com.jamieswhiteshirt.clothesline.api.INetworkManager;

import java.util.List;

public interface IClientNetworkManager extends INetworkManager<IClientNetworkEdge> {
    void addNetwork(INetwork network);

    void reset(List<INetwork> networks);
}
