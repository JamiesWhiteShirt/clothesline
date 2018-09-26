package com.jamieswhiteshirt.clothesline.internal;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.chunk.Chunk;

public interface INetworkManagerWatcher {
    void onPlayerWatchChunk(EntityPlayerMP player, Chunk chunk);

    void onPlayerUnWatchChunk(EntityPlayerMP player, Chunk chunk);
}
