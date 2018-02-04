package com.jamieswhiteshirt.clothesline.common.impl;

import com.jamieswhiteshirt.clothesline.api.Graph;
import com.jamieswhiteshirt.clothesline.api.INetworkNode;
import com.jamieswhiteshirt.clothesline.api.Network;

import java.util.Objects;

public final class NetworkNode implements INetworkNode {
    private final Network network;
    private final Graph.Node graphNode;

    NetworkNode(Network network, Graph.Node graphNode) {
        this.network = network;
        this.graphNode = graphNode;
    }

    @Override
    public Network getNetwork() {
        return network;
    }

    @Override
    public Graph.Node getGraphNode() {
        return graphNode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NetworkNode node = (NetworkNode) o;
        return Objects.equals(network, node.network) &&
            Objects.equals(graphNode, node.graphNode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(network, graphNode);
    }
}
