package com.jamieswhiteshirt.clothesline.client.impl;

import com.jamieswhiteshirt.clothesline.api.Graph;
import com.jamieswhiteshirt.clothesline.api.INetwork;
import com.jamieswhiteshirt.clothesline.api.client.IClientNetworkEdge;
import com.jamieswhiteshirt.clothesline.api.client.IClientNetworkManager;
import com.jamieswhiteshirt.clothesline.api.INetworkNode;
import com.jamieswhiteshirt.clothesline.common.impl.NetworkManager;
import com.jamieswhiteshirt.clothesline.common.impl.NetworkNode;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@SideOnly(Side.CLIENT)
public final class ClientNetworkManager extends NetworkManager<IClientNetworkEdge, INetworkNode> implements IClientNetworkManager {
    public ClientNetworkManager(WorldClient world) {
        super(world);
    }

    @Override
    public void reset(List<INetwork> networks) {
        resetInternal(networks);
    }

    @Override
    protected ClientNetworkEdge createNetworkEdge(Graph.Edge graphEdge, INetwork network, int index) {
        return new ClientNetworkEdge(network, graphEdge, index);
    }

    @Override
    protected INetworkNode createNetworkNode(Graph.Node graphNode, INetwork network) {
        return new NetworkNode(network, graphNode);
    }

    @Override
    public void addNetwork(INetwork network) {
        super.addNetwork(network);
    }

    @Override
    public boolean connect(BlockPos fromPos, BlockPos toPos) {
        if (fromPos.equals(toPos)) {
            return false;
        }

        INetworkNode fromNode = getNodes().get(fromPos);
        INetworkNode toNode = getNodes().get(toPos);

        if (fromNode != null) {
            INetwork fromNetwork = fromNode.getNetwork();
            if (toNode != null) {
                INetwork toNetwork = toNode.getNetwork();

                //TODO: Look into circular networks
                return fromNetwork != toNetwork;
            }
        }

        return true;
    }

    @Override
    public void destroyNode(BlockPos pos) {
    }

    @Override
    public void createNode(BlockPos pos) {
    }
}
