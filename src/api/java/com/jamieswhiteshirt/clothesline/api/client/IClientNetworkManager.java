package com.jamieswhiteshirt.clothesline.api.client;

import com.jamieswhiteshirt.clothesline.api.INetwork;
import com.jamieswhiteshirt.clothesline.api.INetworkManager;
import com.jamieswhiteshirt.clothesline.api.INetworkNode;

import java.util.List;

public interface IClientNetworkManager extends INetworkManager<IClientNetworkEdge, INetworkNode> {
    void addNetwork(INetwork network);

    void reset(List<INetwork> networks);
}
