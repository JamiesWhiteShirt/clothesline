package com.jamieswhiteshirt.clothesline.client.capability;

import com.jamieswhiteshirt.clothesline.Clothesline;
import com.jamieswhiteshirt.clothesline.client.impl.ClientNetworkManager;
import com.jamieswhiteshirt.clothesline.internal.IWorldEventDispatcher;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SideOnly(Side.CLIENT)
public class ClientCapabilityProvider implements ICapabilityProvider {
    private final ClientNetworkManager manager;
    private final IWorldEventDispatcher eventDispatcher = new IWorldEventDispatcher() {
        @Override
        public void onTick() {
            manager.update();
        }

        @Override
        public void onPlayerWatchChunk(EntityPlayerMP player, Chunk chunk) {
        }

        @Override
        public void onPlayerUnWatchChunk(EntityPlayerMP player, Chunk chunk) {
        }

        @Override
        public void onChunkLoaded(int x, int z) {
        }

        @Override
        public void onChunkUnloaded(int x, int z) {
        }
    };

    public ClientCapabilityProvider(ClientNetworkManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == Clothesline.NETWORK_MANAGER_CAPABILITY || capability == Clothesline.WORLD_EVENT_DISPATCHER_CAPABILITY;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == Clothesline.NETWORK_MANAGER_CAPABILITY) {
            return Clothesline.NETWORK_MANAGER_CAPABILITY.cast(manager);
        } else if (capability == Clothesline.WORLD_EVENT_DISPATCHER_CAPABILITY) {
            return Clothesline.WORLD_EVENT_DISPATCHER_CAPABILITY.cast(eventDispatcher);
        }
        return null;
    }
}
