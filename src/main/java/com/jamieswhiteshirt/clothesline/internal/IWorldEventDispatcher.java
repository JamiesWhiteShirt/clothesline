package com.jamieswhiteshirt.clothesline.internal;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.chunk.Chunk;

public interface IWorldEventDispatcher {
    void onTick();

    void onPlayerWatchChunk(EntityPlayerMP player, Chunk chunk);

    void onPlayerUnWatchChunk(EntityPlayerMP player, Chunk chunk);

    void onChunkLoaded(int x, int z);

    void onChunkUnloaded(int x, int z);
}
