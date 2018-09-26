package com.jamieswhiteshirt.clothesline.common.util;

import com.jamieswhiteshirt.clothesline.api.INetwork;
import com.jamieswhiteshirt.clothesline.api.INetworkManager;
import it.unimi.dsi.fastutil.longs.LongArraySet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.util.math.BlockPos;

import java.util.LinkedHashSet;
import java.util.Set;

public class NodeIntersector implements IIntersector {
    @Override
    public LongSet getChunksIntersectingNetwork(INetwork network) {
        LongSet result = new LongArraySet();
        for (BlockPos pos : network.getState().getPath().getNodes().keySet()) {
            long x = pos.getX() >> 4;
            long z = pos.getZ() >> 4;
            result.add((x + 0x7FFFFFFFL) | ((z + 0x7FFFFFFFL) << 32));
        }
        return result;
    }

    @Override
    public Set<INetwork> getNetworksIntersectingChunk(INetworkManager<?, ?> manager, int chunkX, int chunkZ) {
        int xMin = chunkX << 4;
        int zMin = chunkZ << 4;
        int xMax = (chunkX + 1) << 4;
        int zMax = (chunkZ + 1) << 4;
        Set<INetwork> result = new LinkedHashSet<>();
        manager.getNodes()
            .entries(r -> xMin < r.x2() && xMax > r.x1() && zMin < r.z2() && zMax > r.z1())
            .forEach(entry -> result.add(entry.getValue().getNetwork()));
        return result;
    }
}
