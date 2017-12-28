package com.jamieswhiteshirt.clothesline.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.UUID;

public interface INetworkManager {
    interface INetworkNode {
        Network getNetwork();

        NetworkGraph.Node getGraphNode();
    }

    Collection<Network> getNetworks();

    @Nullable
    Network getNetworkByUUID(UUID uuid);

    @Nullable
    INetworkNode getNetworkNodeByPos(BlockPos pos);

    void addNetwork(Network network);

    void removeNetwork(UUID networkUuid);

    void setNetworks(Collection<Network> networks);

    void update();

    boolean connect(BlockPos from, BlockPos to);

    void destroy(BlockPos pos);

    void disconnect(BlockPos posA, BlockPos posB);

    void setNetworkState(Network network, AbsoluteNetworkState state);

    ItemStack insertItem(Network network, int offset, ItemStack stack, boolean simulate);

    ItemStack extractItem(Network network, int offset, boolean simulate);

    void setAttachment(Network network, int offset, ItemStack stack);

    boolean useItem(Network network, EntityPlayer player, EnumHand hand, int offset);

    void hitAttachment(Network network, EntityPlayer player, int offset);

    void addMomentum(Network network, int momentum);

    void addEventListener(INetworkManagerEventListener eventListener);

    void removeEventListener(INetworkManagerEventListener eventListener);
}
