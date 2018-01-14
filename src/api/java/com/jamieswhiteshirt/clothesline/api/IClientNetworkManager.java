package com.jamieswhiteshirt.clothesline.api;

import java.util.List;

public interface IClientNetworkManager extends ICommonNetworkManager {
    void addNetwork(Network network);

    void reset(List<Network> networks);
}
