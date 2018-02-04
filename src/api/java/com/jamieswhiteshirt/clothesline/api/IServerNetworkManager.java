package com.jamieswhiteshirt.clothesline.api;

import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public interface IServerNetworkManager<T extends INetworkEdge> extends INetworkManager<T> {
    void reset(List<PersistentNetwork> networks);

    @Nullable
    Network getNetworkByUuid(UUID uuid);

    void disconnect(BlockPos posA, BlockPos posB);
}
