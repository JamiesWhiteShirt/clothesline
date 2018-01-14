package com.jamieswhiteshirt.clothesline.api;

import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public interface IServerNetworkManager extends ICommonNetworkManager {
    void reset(List<Pair<UUID, AbsoluteNetworkState>> networks);

    @Nullable
    Network getNetworkByUuid(UUID uuid);

    boolean connect(BlockPos from, BlockPos to);

    void destroy(BlockPos pos);

    void disconnect(BlockPos posA, BlockPos posB);
}
