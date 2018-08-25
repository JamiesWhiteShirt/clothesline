package com.jamieswhiteshirt.clothesline.common.capability;

import com.jamieswhiteshirt.clothesline.Clothesline;
import com.jamieswhiteshirt.clothesline.api.PersistentNetwork;
import com.jamieswhiteshirt.clothesline.common.impl.ServerNetworkManager;
import com.jamieswhiteshirt.clothesline.common.util.BasicPersistentNetwork;
import com.jamieswhiteshirt.clothesline.common.util.NBTSerialization;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.stream.Collectors;

public class ServerNetworkManagerProvider implements ICapabilitySerializable<NBTTagList> {
    private final ServerNetworkManager instance;

    public ServerNetworkManagerProvider(ServerNetworkManager instance) {
        this.instance = instance;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == Clothesline.NETWORK_MANAGER_CAPABILITY || capability == Clothesline.SERVER_NETWORK_MANAGER_CAPABILITY;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == Clothesline.NETWORK_MANAGER_CAPABILITY) {
            return Clothesline.NETWORK_MANAGER_CAPABILITY.cast(instance);
        } else if (capability == Clothesline.SERVER_NETWORK_MANAGER_CAPABILITY) {
            return Clothesline.SERVER_NETWORK_MANAGER_CAPABILITY.cast(instance);
        } else {
            return null;
        }
    }

    @Override
    @Nullable
    public NBTTagList serializeNBT() {
        return NBTSerialization.writePersistentNetworks(instance.getNetworks().stream()
            .map(network -> new PersistentNetwork(network.getUuid(), network.getState()))
            .map(BasicPersistentNetwork::fromAbsolute)
            .collect(Collectors.toList()));
    }

    @Override
    public void deserializeNBT(NBTTagList nbt) {
        instance.reset(NBTSerialization.readPersistentNetworks(nbt).stream()
            .map(BasicPersistentNetwork::toAbsolute)
            .collect(Collectors.toList()));
    }
}
