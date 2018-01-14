package com.jamieswhiteshirt.clothesline.client.impl;

import com.jamieswhiteshirt.clothesline.api.IClientNetworkManager;
import com.jamieswhiteshirt.clothesline.api.Network;
import com.jamieswhiteshirt.clothesline.common.impl.CommonNetworkManager;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@SideOnly(Side.CLIENT)
public final class ClientNetworkManager extends CommonNetworkManager implements IClientNetworkManager {
    public ClientNetworkManager(WorldClient world) {
    }

    @Override
    public void reset(List<Network> networks) {
        resetInternal(networks);
    }

    @Override
    public void addNetwork(Network network) {
        super.addNetwork(network);
    }

    @Override
    public void hitAttachment(Network network, EntityPlayer player, int attachmentKey) {
        setAttachment(network, attachmentKey, ItemStack.EMPTY);
    }

    @Override
    public boolean connect(BlockPos fromPos, BlockPos toPos) {
        if (fromPos.equals(toPos)) {
            return false;
        }

        INetworkNode fromNode = getNetworkNodeByPos(fromPos);
        INetworkNode toNode = getNetworkNodeByPos(toPos);

        if (fromNode != null) {
            Network fromNetwork = fromNode.getNetwork();
            if (toNode != null) {
                Network toNetwork = toNode.getNetwork();

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
