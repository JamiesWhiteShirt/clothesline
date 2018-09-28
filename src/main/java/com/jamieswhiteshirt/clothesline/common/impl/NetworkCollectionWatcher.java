package com.jamieswhiteshirt.clothesline.common.impl;

import com.jamieswhiteshirt.clothesline.api.*;
import com.jamieswhiteshirt.clothesline.common.util.ISpanFunction;
import com.jamieswhiteshirt.clothesline.internal.INetworkCollectionWatcher;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerChunkMap;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public final class NetworkCollectionWatcher implements INetworkCollectionWatcher {
    private static final ResourceLocation LISTENER_KEY = new ResourceLocation("clothesline", "watcher");

    private final INetworkCollection networks;
    private final PlayerChunkMap playerChunkMap;
    private final SimpleNetworkWrapper networkChannel;
    private final ISpanFunction spanFunction;
    private final Int2ObjectMap<NetworkWatcher> networkWatchers = new Int2ObjectOpenHashMap<>();

    public NetworkCollectionWatcher(INetworkCollection networks, WorldServer world, SimpleNetworkWrapper networkChannel, ISpanFunction spanFunction) {
        this.networks = networks;
        this.playerChunkMap = world.getPlayerChunkMap();
        this.networkChannel = networkChannel;
        this.spanFunction = spanFunction;

        networks.addEventListener(LISTENER_KEY, new INetworkCollectionListener() {
            @Override
            public void onNetworkAdded(INetworkCollection networks, INetwork network) {
                addNetworkWatcher(network);
            }

            @Override
            public void onNetworkRemoved(INetworkCollection networks, INetwork network) {
                removeNetworkWatcher(network);
            }
        });
    }

    @Override
    public void onPlayerWatchChunk(EntityPlayerMP player, Chunk chunk) {
        for (INetwork network : spanFunction.getNetworkSpanOfChunk(networks, chunk.x, chunk.z)) {
            networkWatchers.get(network.getId()).addPlayer(player);
        }
    }

    @Override
    public void onPlayerUnWatchChunk(EntityPlayerMP player, Chunk chunk) {
        for (INetwork network : spanFunction.getNetworkSpanOfChunk(networks, chunk.x, chunk.z)) {
            networkWatchers.get(network.getId()).removePlayer(player);
        }
    }

    private NetworkWatcher addNetworkWatcher(INetwork network) {
        NetworkWatcher networkWatcher = new NetworkWatcher(network, networkChannel);
        network.addEventListener(LISTENER_KEY, networkWatcher);
        networkWatchers.put(network.getId(), networkWatcher);

        // Players may already be watching chunks that the network intersects with
        for (long position : spanFunction.getChunkSpanOfNetwork(network.getState())) {
            int x = ISpanFunction.chunkX(position);
            int z = ISpanFunction.chunkZ(position);
            PlayerChunkMapEntry entry = playerChunkMap.getEntry(x, z);
            if (entry != null) {
                for (EntityPlayerMP player : entry.getWatchingPlayers()) {
                    networkWatcher.addPlayer(player);
                }
            }
        }

        return networkWatcher;
    }

    private void removeNetworkWatcher(INetwork network) {
        network.removeEventListener(LISTENER_KEY);
        networkWatchers.remove(network.getId()).clear();
    }
}
