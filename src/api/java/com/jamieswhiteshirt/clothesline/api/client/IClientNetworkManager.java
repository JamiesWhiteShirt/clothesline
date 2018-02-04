package com.jamieswhiteshirt.clothesline.api.client;

import com.jamieswhiteshirt.clothesline.api.INetworkManager;
import com.jamieswhiteshirt.clothesline.api.Network;

import java.util.List;

public interface IClientNetworkManager extends INetworkManager<IClientNetworkEdge> {
    void addNetwork(Network network);

    void reset(List<Network> networks);
}
