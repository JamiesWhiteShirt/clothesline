package com.jamieswhiteshirt.clothesline.api;

public interface INetworkEdge {
    INetwork getNetwork();

    Graph.Edge getGraphEdge();

    int getIndex();
}
