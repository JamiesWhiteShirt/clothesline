package com.jamieswhiteshirt.clothesline.common.impl;

import com.jamieswhiteshirt.clothesline.api.Graph;
import com.jamieswhiteshirt.clothesline.api.INetwork;
import com.jamieswhiteshirt.clothesline.api.INetworkEdge;

import java.util.Objects;

public class NetworkEdge implements INetworkEdge {
    private final INetwork network;
    private final Graph.Edge graphEdge;

    public NetworkEdge(INetwork network, Graph.Edge graphEdge) {
        this.network = network;
        this.graphEdge = graphEdge;
    }

    @Override
    public INetwork getNetwork() {
        return network;
    }

    @Override
    public Graph.Edge getGraphEdge() {
        return graphEdge;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NetworkEdge edge = (NetworkEdge) o;
        return Objects.equals(network, edge.network) &&
            Objects.equals(graphEdge, edge.graphEdge);
    }

    @Override
    public int hashCode() {
        return Objects.hash(network, graphEdge);
    }
}