package com.jamieswhiteshirt.clothesline.client.capability;

import com.jamieswhiteshirt.clothesline.api.ICommonNetworkManager;
import com.jamieswhiteshirt.clothesline.api.client.IClientNetworkManager;
import com.jamieswhiteshirt.clothesline.client.impl.ClientNetworkManager;
import com.jamieswhiteshirt.clothesline.common.Util;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ClientNetworkManagerProvider implements ICapabilityProvider {
    @CapabilityInject(ICommonNetworkManager.class)
    private static final Capability<ICommonNetworkManager> COMMON_CAPABILITY = Util.nonNullInjected();
    @CapabilityInject(IClientNetworkManager.class)
    private static final Capability<IClientNetworkManager> CLIENT_CAPABILITY = Util.nonNullInjected();

    private final ClientNetworkManager instance;

    public ClientNetworkManagerProvider(ClientNetworkManager instance) {
        this.instance = instance;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == COMMON_CAPABILITY || capability == CLIENT_CAPABILITY;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == COMMON_CAPABILITY) {
            return COMMON_CAPABILITY.cast(instance);
        } else if (capability == CLIENT_CAPABILITY) {
            return CLIENT_CAPABILITY.cast(instance);
        } else {
            return null;
        }
    }
}
