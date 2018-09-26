package com.jamieswhiteshirt.clothesline.common.util;

import com.jamieswhiteshirt.clothesline.api.INetwork;
import com.jamieswhiteshirt.clothesline.api.INetworkManager;
import it.unimi.dsi.fastutil.longs.LongSet;

import java.util.Set;

public interface IIntersector {
    LongSet getChunksIntersectingNetwork(INetwork network);

    Set<INetwork> getNetworksIntersectingChunk(INetworkManager<?, ?> manager, int chunkX, int chunkZ);

    static int chunkX(long position) {
        return (int)(position - 0x7FFFFFFFL);
    }

    static int chunkZ(long position) {
        return (int)((position >>> 32) - 0x7FFFFFFFL);
    }
}
