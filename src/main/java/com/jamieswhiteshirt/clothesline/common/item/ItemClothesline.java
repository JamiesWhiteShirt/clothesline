package com.jamieswhiteshirt.clothesline.common.item;

import com.jamieswhiteshirt.clothesline.Clothesline;
import com.jamieswhiteshirt.clothesline.api.INetworkManager;
import com.jamieswhiteshirt.clothesline.api.Utility;
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
        INetworkManager manager = world.getCapability(Clothesline.NETWORK_MANAGER_CAPABILITY, null);
        if (manager != null) {
            if (world.getBlockState(to).getBlock() == ClotheslineBlocks.CLOTHESLINE_ANCHOR) {
                RayTraceResult result = world.rayTraceBlocks(Utility.midVec(from), Utility.midVec(to), false, true, false);
                if (result == null || result.typeOfHit == RayTraceResult.Type.MISS) {
                    if (manager.connect(from, to)) {
                        entity.getHeldItem(hand).shrink(1);
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
