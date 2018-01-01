package com.jamieswhiteshirt.clothesline.core;

import com.jamieswhiteshirt.clothesline.core.event.MayPlaceBlockEvent;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class CommonHooks {
    public static boolean onMayPlace(World world, Block block, BlockPos pos) {
        return MinecraftForge.EVENT_BUS.post(new MayPlaceBlockEvent(world, pos, block.getDefaultState()));
    }
}
