package com.jamieswhiteshirt.clothesline.api;

public interface INetworkEdge {
    INetwork getNetwork();

    Path.Edge getPathEdge();

    int getIndex();
}
