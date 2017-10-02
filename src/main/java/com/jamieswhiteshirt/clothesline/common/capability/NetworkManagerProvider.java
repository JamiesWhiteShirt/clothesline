package com.jamieswhiteshirt.clothesline.common.capability;

import com.jamieswhiteshirt.clothesline.api.INetworkManager;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NetworkManagerProvider implements ICapabilitySerializable<NBTTagList> {
    @CapabilityInject(INetworkManager.class)
    private static final Capability<INetworkManager> CAPABILITY = null;

    private final NetworkManager instance;

    public NetworkManagerProvider(World world) {
        instance = new NetworkManager(world);
    }

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
    public NBTTagList serializeNBT() {
        return (NBTTagList) CAPABILITY.getStorage().writeNBT(CAPABILITY, instance, null);
    }

    @Override
    public void deserializeNBT(NBTTagList nbt) {
        CAPABILITY.getStorage().readNBT(CAPABILITY, instance, null, nbt);
    }
}
