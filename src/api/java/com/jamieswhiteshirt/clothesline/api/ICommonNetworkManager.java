package com.jamieswhiteshirt.clothesline.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.UUID;

public interface ICommonNetworkManager {
    interface INetworkNode {
        Network getNetwork();

        NetworkGraph.Node getGraphNode();
    }

    Collection<Network> getNetworks();

    @Nullable
    Network getNetworkById(int id);

    @Nullable
    INetworkNode getNetworkNodeByPos(BlockPos pos);

    void removeNetwork(Network network);

    void update();

    void setNetworkState(Network network, AbsoluteNetworkState state);

    ItemStack insertItem(Network network, int attachmentKey, ItemStack stack, boolean simulate);

    ItemStack extractItem(Network network, int attachmentKey, boolean simulate);

    void setAttachment(Network network, int attachmentKey, ItemStack stack);

    boolean useItem(Network network, EntityPlayer player, EnumHand hand, int attachmentKey);

    void hitAttachment(Network network, EntityPlayer player, int attachmentKey);

    void addMomentum(Network network, int momentum);

    void addEventListener(INetworkManagerEventListener eventListener);

    void removeEventListener(INetworkManagerEventListener eventListener);
}
