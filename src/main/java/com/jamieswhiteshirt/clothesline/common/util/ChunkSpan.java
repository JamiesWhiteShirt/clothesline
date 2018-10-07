package com.jamieswhiteshirt.clothesline.common.util;

import com.jamieswhiteshirt.clothesline.api.Path;
import it.unimi.dsi.fastutil.longs.LongArraySet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

public class ChunkSpan {
    public static LongSet ofPath(Path path) {
        LongSet result = new LongArraySet();
        for (BlockPos pos : path.getNodes().keySet()) {
            int x = pos.getX() >> 4;
            int z = pos.getZ() >> 4;
            result.add(ChunkPos.asLong(x, z));
        }
        return LongSets.unmodifiable(result);
    }
}
