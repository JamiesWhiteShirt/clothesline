package com.jamieswhiteshirt.clothesline.client.impl;

import com.jamieswhiteshirt.clothesline.api.Graph;
import com.jamieswhiteshirt.clothesline.api.INetwork;
import com.jamieswhiteshirt.clothesline.api.client.IClientNetworkEdge;
import com.jamieswhiteshirt.clothesline.api.client.IClientNetworkManager;
import com.jamieswhiteshirt.clothesline.api.INetworkNode;
import com.jamieswhiteshirt.clothesline.common.impl.NetworkManager;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@SideOnly(Side.CLIENT)
public final class ClientNetworkManager extends NetworkManager<IClientNetworkEdge> implements IClientNetworkManager {
    public ClientNetworkManager(WorldClient world) {
    }

    @Override
    public void reset(List<INetwork> networks) {
        resetInternal(networks);
    }

    @Override
    protected ClientNetworkEdge createNetworkEdge(INetwork network, Graph.Edge graphEdge) {
        return new ClientNetworkEdge(network, graphEdge);
    }

    @Override
    public void addNetwork(INetwork network) {
        super.addNetwork(network);
    }

    @Override
    public void hitAttachment(INetwork network, EntityPlayer player, int attachmentKey) {
        network.setAttachment(attachmentKey, ItemStack.EMPTY);
    }

    @Override
    public boolean connect(BlockPos fromPos, BlockPos toPos) {
        if (fromPos.equals(toPos)) {
            return false;
        }

        INetworkNode fromNode = getNetworkNodeByPos(fromPos);
        INetworkNode toNode = getNetworkNodeByPos(toPos);

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
    public void destroy(BlockPos pos) {
    }
}
