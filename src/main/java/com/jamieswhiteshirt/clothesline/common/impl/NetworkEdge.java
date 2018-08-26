package com.jamieswhiteshirt.clothesline.common.impl;

import com.jamieswhiteshirt.clothesline.api.Path;
import com.jamieswhiteshirt.clothesline.api.INetwork;
import com.jamieswhiteshirt.clothesline.api.INetworkEdge;

import java.util.Objects;

public class NetworkEdge implements INetworkEdge {
    private final INetwork network;
    private final Path.Edge pathEdge;
    private final int index;

    public NetworkEdge(INetwork network, Path.Edge pathEdge, int index) {
        this.network = network;
        this.pathEdge = pathEdge;
        this.index = index;
    }

    @Override
    public INetwork getNetwork() {
        return network;
    }

    @Override
    public Path.Edge getPathEdge() {
        return pathEdge;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NetworkEdge edge = (NetworkEdge) o;
        return index == edge.index &&
            Objects.equals(pathEdge, edge.pathEdge);
    }

    @Override
    public int hashCode() {
        return Objects.hash(network, index);
    }
}
