package com.jamieswhiteshirt.clothesline.common.util;

import com.jamieswhiteshirt.clothesline.api.INetwork;
import com.jamieswhiteshirt.clothesline.api.INetworkCollection;
import com.jamieswhiteshirt.clothesline.api.INetworkState;
import it.unimi.dsi.fastutil.longs.LongSet;

import java.util.Set;

public interface ISpanFunction {
    LongSet getChunkSpanOfNetwork(INetworkState state);

    Set<INetwork> getNetworkSpanOfChunk(INetworkCollection networks, int chunkX, int chunkZ);

    static long chunkPosition(int x, int z) {
        return ((long)x + 0x7FFFFFFFL) | (((long)z + 0x7FFFFFFFL) << 32);
    }

    static int chunkX(long position) {
        return (int)(position - 0x7FFFFFFFL);
    }

    static int chunkZ(long position) {
        return (int)((position >>> 32) - 0x7FFFFFFFL);
    }
}
