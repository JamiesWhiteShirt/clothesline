package com.jamieswhiteshirt.clothesline.api;

import com.jamieswhiteshirt.rtree3i.RTree;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.Collection;

public interface INetworkManager<T extends INetworkEdge> {
    Collection<INetwork> getNetworks();

    @Nullable
    INetwork getNetworkById(int id);

    @Nullable
    INetworkNode getNetworkNodeByPos(BlockPos pos);

    RTree<T> getNetworkEdges();

    void removeNetwork(INetwork network);

    void update();

    boolean connect(BlockPos fromPos, BlockPos toPos);

    void destroy(BlockPos pos);

    void addEventListener(ResourceLocation key, INetworkManagerEventListener<T> eventListener);

    void removeEventListener(ResourceLocation key);
}
