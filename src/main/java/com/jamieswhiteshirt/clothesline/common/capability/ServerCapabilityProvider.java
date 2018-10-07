package com.jamieswhiteshirt.clothesline.common.capability;

import com.jamieswhiteshirt.clothesline.Clothesline;
import com.jamieswhiteshirt.clothesline.api.INetworkManager;
import com.jamieswhiteshirt.clothesline.common.util.BasicPersistentNetwork;
import com.jamieswhiteshirt.clothesline.common.util.NBTSerialization;
import com.jamieswhiteshirt.clothesline.internal.INetworkCollectionTracker;
import com.jamieswhiteshirt.clothesline.internal.INetworkProvider;
import com.jamieswhiteshirt.clothesline.internal.IWorldEventDispatcher;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.stream.Collectors;

public class ServerCapabilityProvider implements ICapabilitySerializable<NBTBase> {
    private final INetworkManager manager;
    private final INetworkProvider provider;
    private final INetworkCollectionTracker<EntityPlayerMP> tracker;
    private final IWorldEventDispatcher eventDispatcher = new IWorldEventDispatcher() {
        @Override
        public void onTick() {
            manager.update();
            tracker.update();
        }

        @Override
        public void onPlayerWatchChunk(EntityPlayerMP player, Chunk chunk) {
            tracker.onWatchChunk(player, chunk.x, chunk.z);
        }

        @Override
        public void onPlayerUnWatchChunk(EntityPlayerMP player, Chunk chunk) {
            tracker.onUnWatchChunk(player, chunk.x, chunk.z);
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

    public ServerCapabilityProvider(INetworkManager manager, INetworkProvider provider, INetworkCollectionTracker<EntityPlayerMP> tracker) {
        this.manager = manager;
        this.provider = provider;
        this.tracker = tracker;
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
    public void deserializeNBT(NBTBase nbt) {
        if (nbt == null) {
            Clothesline.logger.error("Invalid save data. Expected tag compound, found none. Discarding save data.");
            return;
        }
        if (!(nbt instanceof NBTTagCompound)) {
            Clothesline.logger.error("Invalid save data. Expected tag compound, found something else. Discarding save data.");
            return;
        }
        NBTTagCompound compound = (NBTTagCompound)nbt;

        int version;
        if (!compound.hasKey("Version", Constants.NBT.TAG_INT)) {
            Clothesline.logger.warn("Invalid save data. Expected a Version, found no Version. Assuming Version 0.");
            version = 0;
        } else {
            version = compound.getInteger("Version");
        }

        if (version < 0 || version > 0) {
            Clothesline.logger.error("Invalid save data. Expected Version <= 0, found " + version + ". Discarding save data.");
            return;
        }

        if (!compound.hasKey("Networks", Constants.NBT.TAG_LIST)) {
            Clothesline.logger.error("Invalid save data. Expected list of Networks, found none. Discarding save data.");
            return;
        }

        NBTTagList networks = compound.getTagList("Networks", Constants.NBT.TAG_COMPOUND);
        provider.reset(NBTSerialization.readPersistentNetworks(networks).stream()
            .map(BasicPersistentNetwork::toAbsolute)
            .collect(Collectors.toList()));
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setInteger("Version", 0);
        compound.setTag(
            "Networks",
            NBTSerialization.writePersistentNetworks(provider.getNetworks().stream()
                .map(BasicPersistentNetwork::fromAbsolute)
                .collect(Collectors.toList()))
        );
        return compound;
    }
}
