package com.jamieswhiteshirt.clothesline.common.item;

import com.jamieswhiteshirt.clothesline.api.IAttacher;
import com.jamieswhiteshirt.clothesline.api.INetworkManager;
import com.jamieswhiteshirt.clothesline.common.ClotheslineBlocks;
import com.jamieswhiteshirt.clothesline.common.Util;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

import javax.annotation.Nullable;

public class ItemClothesline extends Item {
    @CapabilityInject(IAttacher.class)
    private static final Capability<IAttacher> ATTACHER_CAPABILITY = Util.nonNullInjected();
    @CapabilityInject(INetworkManager.class)
    private static final Capability<INetworkManager> NETWORK_MANAGER_CAPABILITY = null;

    @Nullable
    private BlockPos rayTraceClotheslineAnchor(World world, EntityPlayer player) {
        RayTraceResult result = rayTrace(world, player, true);
        if (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK) {
            IBlockState state = world.getBlockState(result.getBlockPos());
            if (state.getBlock() == ClotheslineBlocks.CLOTHESLINE_ANCHOR) {
                return result.getBlockPos();
            }
        }
        return null;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        IAttacher attacher = player.getCapability(ATTACHER_CAPABILITY, null);
        if (attacher != null) {
            BlockPos attachPos = rayTraceClotheslineAnchor(world, player);
            if (attachPos != null) {
                player.setActiveHand(hand);
                attacher.setAttachPosition(attachPos);
                return new ActionResult<>(EnumActionResult.SUCCESS, stack);
            }
        }
        return new ActionResult<>(EnumActionResult.FAIL, stack);
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
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase entity, int timeLeft) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            IAttacher attacher = player.getCapability(ATTACHER_CAPABILITY, null);
            INetworkManager manager = world.getCapability(NETWORK_MANAGER_CAPABILITY, null);
            if (attacher != null && manager != null) {
                BlockPos from = attacher.getAttachPosition();
                BlockPos to = rayTraceClotheslineAnchor(world, player);
                if (from != null && to != null) {
                    manager.connect(from, to);
                }
            }
        }
    }
}
