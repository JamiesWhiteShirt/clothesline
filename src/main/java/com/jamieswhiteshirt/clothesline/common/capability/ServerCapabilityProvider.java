package com.jamieswhiteshirt.clothesline.common.capability;

import com.jamieswhiteshirt.clothesline.Clothesline;
import com.jamieswhiteshirt.clothesline.api.INetworkManager;
import com.jamieswhiteshirt.clothesline.common.util.BasicPersistentNetwork;
import com.jamieswhiteshirt.clothesline.common.util.NBTSerialization;
import com.jamieswhiteshirt.clothesline.internal.INetworkCollectionTracker;
import com.jamieswhiteshirt.clothesline.internal.INetworkProvider;
import com.jamieswhiteshirt.clothesline.internal.IWorldEventDispatcher;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nullable;
import java.util.stream.Collectors;

public class ServerCapabilityProvider implements ICapabilitySerializable<NBTTagList> {
    private final INetworkManager manager;
    private final INetworkProvider provider;
    private final INetworkCollectionTracker<EntityPlayerMP> watcher;
    private final IWorldEventDispatcher eventDispatcher = new IWorldEventDispatcher() {
        @Override
        public void onTick() {
            manager.update();
        }

        @Override
        public void onPlayerWatchChunk(EntityPlayerMP player, Chunk chunk) {
            watcher.onWatchChunk(player, chunk);
        }

        @Override
        public void onPlayerUnWatchChunk(EntityPlayerMP player, Chunk chunk) {
            watcher.onUnWatchChunk(player, chunk);
        }

        @Override
        public void onChunkLoaded(int x, int z) {
            provider.onChunkLoaded(x, z);
        }

        @Override
        public void onChunkUnloaded(int x, int z) {
            provider.onChunkUnloaded(x, z);
        }
    };

    public ServerCapabilityProvider(INetworkManager manager, INetworkProvider provider, INetworkCollectionTracker<EntityPlayerMP> watcher) {
        this.manager = manager;
        this.provider = provider;
        this.watcher = watcher;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == Clothesline.NETWORK_MANAGER_CAPABILITY || capability == Clothesline.WORLD_EVENT_DISPATCHER_CAPABILITY;
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == Clothesline.NETWORK_MANAGER_CAPABILITY) {
            return Clothesline.NETWORK_MANAGER_CAPABILITY.cast(manager);
        } else if (capability == Clothesline.WORLD_EVENT_DISPATCHER_CAPABILITY) {
            return Clothesline.WORLD_EVENT_DISPATCHER_CAPABILITY.cast(eventDispatcher);
        }
        return null;
    }

    @Override
    @Nullable
    public NBTTagList serializeNBT() {
        return NBTSerialization.writePersistentNetworks(provider.getNetworks().stream()
            .map(BasicPersistentNetwork::fromAbsolute)
            .collect(Collectors.toList()));
    }

    @Override
    public void deserializeNBT(NBTTagList nbt) {
        provider.reset(NBTSerialization.readPersistentNetworks(nbt).stream()
            .map(BasicPersistentNetwork::toAbsolute)
            .collect(Collectors.toList()));
    }
}
