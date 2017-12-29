package com.jamieswhiteshirt.clothesline.common.item;

import com.jamieswhiteshirt.clothesline.api.IConnectionHolder;
import com.jamieswhiteshirt.clothesline.common.Util;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

import javax.annotation.Nullable;

public abstract class ItemConnector extends Item {
    @CapabilityInject(IConnectionHolder.class)
    private static final Capability<IConnectionHolder> CONNECTION_HOLDER_CAPABILITY = Util.nonNullInjected();

    private final ThreadLocal<BlockPos> toPos = new ThreadLocal<>();

    public abstract boolean connectFrom(EntityLivingBase entity, World world, EnumHand hand, BlockPos pos);

    public abstract boolean connect(EntityLivingBase entity, World world, EnumHand hand, BlockPos from, BlockPos to);

    public final void stopActiveHandWithToPos(EntityLivingBase entity, BlockPos pos) {
        toPos.set(pos);
        entity.stopActiveHand();
        toPos.set(null);
    }

    @Nullable
    public final BlockPos getFromPos(EntityLivingBase entity) {
        IConnectionHolder connectionHolder = entity.getCapability(CONNECTION_HOLDER_CAPABILITY, null);
        if (connectionHolder != null) {
            return connectionHolder.getFromPos();
        }
        return null;
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase entity, int timeLeft) {
        IConnectionHolder connectionHolder = entity.getCapability(CONNECTION_HOLDER_CAPABILITY, null);
        if (connectionHolder != null) {
            BlockPos from = connectionHolder.getFromPos();
            BlockPos to = toPos.get();
            if (from != null && to != null) {
                connect(entity, world, entity.getActiveHand(), from, to);
            }
            connectionHolder.setFromPos(null);
        }
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.NONE;
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        IConnectionHolder connectionHolder = player.getCapability(CONNECTION_HOLDER_CAPABILITY, null);
        if (connectionHolder != null && connectFrom(player, world, hand, pos)) {
            player.setActiveHand(hand);
            connectionHolder.setFromPos(pos);
            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.FAIL;
    }
}
