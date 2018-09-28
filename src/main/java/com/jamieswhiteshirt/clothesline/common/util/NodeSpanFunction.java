package com.jamieswhiteshirt.clothesline.common.util;

import com.jamieswhiteshirt.clothesline.api.INetwork;
import com.jamieswhiteshirt.clothesline.api.INetworkCollection;
import com.jamieswhiteshirt.clothesline.api.INetworkState;
import it.unimi.dsi.fastutil.longs.LongArraySet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.util.math.BlockPos;

import java.util.LinkedHashSet;
import java.util.Set;

public class NodeSpanFunction implements ISpanFunction {
    @Override
    public LongSet getChunkSpanOfNetwork(INetworkState state) {
        LongSet result = new LongArraySet();
        for (BlockPos pos : state.getPath().getNodes().keySet()) {
            int x = pos.getX() >> 4;
            int z = pos.getZ() >> 4;
            result.add(ISpanFunction.chunkPosition(x, z));
        }
        return result;
    }

    @Override
    public Set<INetwork> getNetworkSpanOfChunk(INetworkCollection networks, int chunkX, int chunkZ) {
        int xMin = chunkX << 4;
        int zMin = chunkZ << 4;
        int xMax = (chunkX + 1) << 4;
        int zMax = (chunkZ + 1) << 4;
        Set<INetwork> result = new LinkedHashSet<>();
        networks.getNodes()
            .entries(r -> xMin < r.x2() && xMax > r.x1() && zMin < r.z2() && zMax > r.z1())
            .forEach(entry -> result.add(entry.getValue().getNetwork()));
        return result;
    }
}
