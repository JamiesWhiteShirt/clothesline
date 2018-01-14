package com.jamieswhiteshirt.clothesline.common.capability;

import com.jamieswhiteshirt.clothesline.api.ICommonNetworkManager;
import com.jamieswhiteshirt.clothesline.api.IServerNetworkManager;
import com.jamieswhiteshirt.clothesline.common.Util;
import com.jamieswhiteshirt.clothesline.common.impl.ServerNetworkManager;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ServerNetworkManagerProvider implements ICapabilitySerializable<NBTBase> {
    @CapabilityInject(ICommonNetworkManager.class)
    private static final Capability<ICommonNetworkManager> COMMON_CAPABILITY = Util.nonNullInjected();
    @CapabilityInject(IServerNetworkManager.class)
    private static final Capability<IServerNetworkManager> SERVER_CAPABILITY = Util.nonNullInjected();

    private final ServerNetworkManager instance;

    public ServerNetworkManagerProvider(ServerNetworkManager instance) {
        this.instance = instance;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == COMMON_CAPABILITY || capability == SERVER_CAPABILITY;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == COMMON_CAPABILITY) {
            return COMMON_CAPABILITY.cast(instance);
        } else if (capability == SERVER_CAPABILITY) {
            return SERVER_CAPABILITY.cast(instance);
        } else {
            return null;
        }
    }

    @Override
    @Nullable
    public NBTBase serializeNBT() {
        return SERVER_CAPABILITY.getStorage().writeNBT(SERVER_CAPABILITY, instance, null);
    }

    @Override
    public void deserializeNBT(NBTBase nbt) {
        SERVER_CAPABILITY.getStorage().readNBT(SERVER_CAPABILITY, instance, null, nbt);
    }
}
