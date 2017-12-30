package com.jamieswhiteshirt.clothesline.common.item;

import com.jamieswhiteshirt.clothesline.api.INetworkManager;
import com.jamieswhiteshirt.clothesline.api.Measurements;
import com.jamieswhiteshirt.clothesline.common.ClotheslineBlocks;
import com.jamieswhiteshirt.clothesline.common.Util;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class ItemClothesline extends ItemConnector {
    @CapabilityInject(INetworkManager.class)
    private static final Capability<INetworkManager> NETWORK_MANAGER_CAPABILITY = Util.nonNullInjected();

    @Override
    public boolean connectFrom(EntityLivingBase entity, World world, EnumHand hand, BlockPos pos) {
        return world.getBlockState(pos).getBlock() == ClotheslineBlocks.CLOTHESLINE_ANCHOR;
    }

    @Override
    public boolean connect(EntityLivingBase entity, World world, EnumHand hand, BlockPos from, BlockPos to) {
        INetworkManager manager = world.getCapability(NETWORK_MANAGER_CAPABILITY, null);
        if (manager != null) {
            if (world.getBlockState(to).getBlock() == ClotheslineBlocks.CLOTHESLINE_ANCHOR) {
                RayTraceResult result = world.rayTraceBlocks(Measurements.midVec(from), Measurements.midVec(to), false, true, false);
                if (result == null || result.typeOfHit == RayTraceResult.Type.MISS) {
                    if (!world.isRemote) {
                        manager.connect(from, to);
                    }
                    return true;
                }
            }
        }
        return false;
    }
}
