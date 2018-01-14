package com.jamieswhiteshirt.clothesline.common.item;

import com.jamieswhiteshirt.clothesline.Clothesline;
import com.jamieswhiteshirt.clothesline.api.ICommonNetworkManager;
import com.jamieswhiteshirt.clothesline.api.Measurements;
import com.jamieswhiteshirt.clothesline.common.ClotheslineBlocks;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class ItemClothesline extends ItemConnector {
    @Override
    public boolean connectFrom(EntityLivingBase entity, World world, EnumHand hand, BlockPos pos) {
        return world.getBlockState(pos).getBlock() == ClotheslineBlocks.CLOTHESLINE_ANCHOR;
    }

    @Override
    public boolean connect(EntityLivingBase entity, World world, EnumHand hand, BlockPos from, BlockPos to) {
        ICommonNetworkManager manager = world.getCapability(Clothesline.COMMON_NETWORK_MANAGER_CAPABILITY, null);
        if (manager != null) {
            if (world.getBlockState(to).getBlock() == ClotheslineBlocks.CLOTHESLINE_ANCHOR) {
                RayTraceResult result = world.rayTraceBlocks(Measurements.midVec(from), Measurements.midVec(to), false, true, false);
                if (result == null || result.typeOfHit == RayTraceResult.Type.MISS) {
                    return manager.connect(from, to);
                }
            }
        }
        return false;
    }
}
