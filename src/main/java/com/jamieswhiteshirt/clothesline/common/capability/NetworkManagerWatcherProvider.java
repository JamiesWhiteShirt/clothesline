package com.jamieswhiteshirt.clothesline.common.capability;

import com.jamieswhiteshirt.clothesline.Clothesline;
import com.jamieswhiteshirt.clothesline.internal.INetworkManagerWatcher;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NetworkManagerWatcherProvider implements ICapabilityProvider {
    private final INetworkManagerWatcher instance;

    public NetworkManagerWatcherProvider(INetworkManagerWatcher instance) {
        this.instance = instance;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == Clothesline.NETWORK_MANAGER_WATCHER_CAPABILITY;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == Clothesline.NETWORK_MANAGER_WATCHER_CAPABILITY) {
            return Clothesline.NETWORK_MANAGER_WATCHER_CAPABILITY.cast(instance);
        }
        return null;
    }
}
