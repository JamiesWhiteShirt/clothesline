package com.jamieswhiteshirt.clothesline.common.capability;

import com.jamieswhiteshirt.clothesline.api.IConnectionHolder;
import com.jamieswhiteshirt.clothesline.common.Util;
import com.jamieswhiteshirt.clothesline.common.impl.ConnectionHolder;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AttacherProvider implements ICapabilitySerializable<NBTTagCompound> {
    @CapabilityInject(IConnectionHolder.class)
    private static final Capability<IConnectionHolder> CAPABILITY = Util.nonNullInjected();

    private final ConnectionHolder instance = new ConnectionHolder();

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CAPABILITY;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CAPABILITY) {
            return CAPABILITY.cast(instance);
        } else {
            return null;
        }
    }

    @Override
    public NBTTagCompound serializeNBT() {
        return (NBTTagCompound) CAPABILITY.getStorage().writeNBT(CAPABILITY, instance, null);
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        CAPABILITY.getStorage().readNBT(CAPABILITY, instance, null, nbt);
    }
}
