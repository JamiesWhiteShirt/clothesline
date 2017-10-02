package com.jamieswhiteshirt.clothesline.api;

import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;

public interface INetworkManager {
    Map<UUID, Network> getNetworks();

    @Nullable
    Network getNetworkByUUID(UUID uuid);

    @Nullable
    Network getNetworkByBlockPos(BlockPos pos);

    void addNetwork(Network network);

    void removeNetwork(UUID networkUuid);

    void setNetworks(Map<UUID, Network> networks);

    void update();

    boolean connect(BlockPos from, BlockPos to);
}
