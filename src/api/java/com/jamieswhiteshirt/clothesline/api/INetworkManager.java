package com.jamieswhiteshirt.clothesline.api;

import com.jamieswhiteshirt.rtree3i.RTreeMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.Collection;

public interface INetworkManager<E extends INetworkEdge, N extends INetworkNode> {
    Collection<INetwork> getNetworks();

    @Nullable
    INetwork getNetworkById(int id);

    RTreeMap<Line, E> getEdges();

    RTreeMap<BlockPos, N> getNodes();

    void removeNetwork(INetwork network);

    void update();

    boolean connect(BlockPos fromPos, BlockPos toPos);

    void createNode(BlockPos pos);

    void destroyNode(BlockPos pos);

    void addEventListener(ResourceLocation key, INetworkManagerEventListener<E, N> eventListener);

    void removeEventListener(ResourceLocation key);
}
