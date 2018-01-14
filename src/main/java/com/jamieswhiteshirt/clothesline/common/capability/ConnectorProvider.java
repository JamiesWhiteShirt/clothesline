package com.jamieswhiteshirt.clothesline.common.capability;

import com.jamieswhiteshirt.clothesline.Clothesline;
import com.jamieswhiteshirt.clothesline.common.impl.Connector;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ConnectorProvider implements ICapabilitySerializable<NBTTagCompound> {
    private final Connector instance = new Connector();

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == Clothesline.CONNECTOR_CAPABILITY;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == Clothesline.CONNECTOR_CAPABILITY) {
            return Clothesline.CONNECTOR_CAPABILITY.cast(instance);
        } else {
            return null;
        }
    }

    @Override
    public NBTTagCompound serializeNBT() {
        return (NBTTagCompound) Clothesline.CONNECTOR_CAPABILITY.getStorage().writeNBT(Clothesline.CONNECTOR_CAPABILITY, instance, null);
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        Clothesline.CONNECTOR_CAPABILITY.getStorage().readNBT(Clothesline.CONNECTOR_CAPABILITY, instance, null, nbt);
    }
}
