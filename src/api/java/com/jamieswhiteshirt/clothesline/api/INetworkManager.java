package com.jamieswhiteshirt.clothesline.api;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.UUID;

public interface INetworkManager {
    Collection<Network> getNetworks();

    @Nullable
    Network getNetworkByUUID(UUID uuid);

    @Nullable
    Network getNetworkByBlockPos(BlockPos pos);

    void addNetwork(Network network);

    void removeNetwork(UUID networkUuid);

    void setNetworks(Collection<Network> networks);

    void update();

    boolean connect(BlockPos from, BlockPos to);

    void destroy(BlockPos pos);

    void setNetworkState(Network network, AbsoluteNetworkState state);

    ItemStack insertItem(Network network, int offset, ItemStack stack, boolean simulate);

    ItemStack extractItem(Network network, int offset, boolean simulate);

    void setAttachment(Network network, int offset, ItemStack stack);

    void addMomentum(Network network, int momentum);

    void addEventListener(INetworkManagerEventListener eventListener);

    void removeEventListener(INetworkManagerEventListener eventListener);
}
