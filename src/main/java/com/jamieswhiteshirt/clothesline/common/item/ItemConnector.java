package com.jamieswhiteshirt.clothesline.common.item;

import com.jamieswhiteshirt.clothesline.Clothesline;
import com.jamieswhiteshirt.clothesline.api.IActivityMovement;
import com.jamieswhiteshirt.clothesline.api.IConnector;
import com.jamieswhiteshirt.clothesline.common.Util;
import com.jamieswhiteshirt.clothesline.common.network.message.MessageSetConnectorPos;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class ItemConnector extends Item {
    @CapabilityInject(IConnector.class)
    private static final Capability<IConnector> CONNECTOR_CAPABILITY = Util.nonNullInjected();

    @CapabilityInject(IActivityMovement.class)
    private static final Capability<IActivityMovement> ACTIVITY_MOVEMENT_CAPABILITY = Util.nonNullInjected();

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
        IConnector connector = entity.getCapability(CONNECTOR_CAPABILITY, null);
        if (connector != null) {
            return connector.getPos();
        }
        return null;
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase entity, int timeLeft) {
        IConnector connector = entity.getCapability(CONNECTOR_CAPABILITY, null);
        if (connector != null) {
            BlockPos from = connector.getPos();
            BlockPos to = toPos.get();
            if (from != null && to != null) {
                connect(entity, world, entity.getActiveHand(), from, to);
            }
            setConnectorPos(world, entity, connector, null);
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
        IConnector connector = player.getCapability(CONNECTOR_CAPABILITY, null);
        if (connector != null && connectFrom(player, world, hand, pos)) {
            player.setActiveHand(hand);
            setConnectorPos(world, player, connector, pos);
            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.FAIL;
    }

    private void setConnectorPos(World world, EntityLivingBase entity, IConnector connector, @Nullable BlockPos pos) {
        if (!world.isRemote) {
            Packet<?> packet = Clothesline.instance.networkWrapper.getPacketFrom(new MessageSetConnectorPos(entity.getEntityId(), pos));
            ((WorldServer) world).getEntityTracker().sendToTracking(entity, packet);
        }
        connector.setPos(pos);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new ICapabilityProvider() {
            @Override
            public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
                return capability == ACTIVITY_MOVEMENT_CAPABILITY;
            }

            @Nullable
            @Override
            public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
                return capability == ACTIVITY_MOVEMENT_CAPABILITY ? ACTIVITY_MOVEMENT_CAPABILITY.cast((IActivityMovement) player -> false) : null;
            }
        };
    }
}
