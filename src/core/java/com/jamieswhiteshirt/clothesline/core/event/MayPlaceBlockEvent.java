package com.jamieswhiteshirt.clothesline.core.event;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

/**
 * Cancel the event to prevent block placement.
 */
@Cancelable
public class MayPlaceBlockEvent extends BlockEvent {
    public MayPlaceBlockEvent(World world, BlockPos pos, IBlockState state) {
        super(world, pos, state);
    }
}
