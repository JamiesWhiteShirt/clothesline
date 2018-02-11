package com.jamieswhiteshirt.clothesline.client.capability;

import com.jamieswhiteshirt.clothesline.Clothesline;
import com.jamieswhiteshirt.clothesline.client.impl.ClientNetworkManager;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ClientNetworkManagerProvider implements ICapabilityProvider {
    private final ClientNetworkManager instance;

    public ClientNetworkManagerProvider(ClientNetworkManager instance) {
        this.instance = instance;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == Clothesline.NETWORK_MANAGER_CAPABILITY || capability == Clothesline.CLIENT_NETWORK_MANAGER_CAPABILITY;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == Clothesline.NETWORK_MANAGER_CAPABILITY) {
            return Clothesline.NETWORK_MANAGER_CAPABILITY.cast(instance);
        } else if (capability == Clothesline.CLIENT_NETWORK_MANAGER_CAPABILITY) {
            return Clothesline.CLIENT_NETWORK_MANAGER_CAPABILITY.cast(instance);
        } else {
            return null;
        }
    }
}
