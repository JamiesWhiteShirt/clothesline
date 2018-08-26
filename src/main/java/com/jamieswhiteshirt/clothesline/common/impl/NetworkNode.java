package com.jamieswhiteshirt.clothesline.common.impl;

import com.jamieswhiteshirt.clothesline.api.Path;
import com.jamieswhiteshirt.clothesline.api.INetwork;
import com.jamieswhiteshirt.clothesline.api.INetworkNode;

import java.util.Objects;

public final class NetworkNode implements INetworkNode {
    private final INetwork network;
    private final Path.Node pathNode;

    public NetworkNode(INetwork network, Path.Node pathNode) {
        this.network = network;
        this.pathNode = pathNode;
    }

    @Override
    public INetwork getNetwork() {
        return network;
    }

    @Override
    public Path.Node getPathNode() {
        return pathNode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NetworkNode node = (NetworkNode) o;
        return Objects.equals(network, node.network) &&
            Objects.equals(pathNode, node.pathNode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(network, pathNode);
    }
}
