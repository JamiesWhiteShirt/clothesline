package com.jamieswhiteshirt.clothesline.common.impl;

import com.jamieswhiteshirt.clothesline.api.*;
import com.jamieswhiteshirt.clothesline.common.util.IIntersector;
import com.jamieswhiteshirt.clothesline.internal.INetworkManagerWatcher;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerChunkMap;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

import java.util.*;

public final class NetworkManagerWatcher<E extends INetworkEdge, N extends INetworkNode> implements INetworkManagerWatcher {
    private static final ResourceLocation LISTENER_KEY = new ResourceLocation("clothesline", "watcher");

    private final INetworkManager<E, N> manager;
    private final PlayerChunkMap playerChunkMap;
    private final SimpleNetworkWrapper networkChannel;
    private final IIntersector intersector;
    private Int2ObjectMap<NetworkWatcher> networkWatchers = new Int2ObjectOpenHashMap<>();

    public NetworkManagerWatcher(INetworkManager<E, N> manager, WorldServer world, SimpleNetworkWrapper networkChannel, IIntersector intersector) {
        this.manager = manager;
        this.playerChunkMap = world.getPlayerChunkMap();
        this.networkChannel = networkChannel;
        this.intersector = intersector;

        manager.addEventListener(LISTENER_KEY, new INetworkManagerListener<E, N>() {
            @Override
            public void onNetworksReset(INetworkManager<E, N> networkManager, List<INetwork> previousNetworks, List<INetwork> newNetworks) {
                for (INetwork network : previousNetworks) {
                    removeNetworkWatcher(network);
                }
                networkWatchers = new Int2ObjectOpenHashMap<>();
                for (INetwork network : newNetworks) {
                    addNetworkWatcher(network);
                }
            }

            @Override
            public void onNetworkAdded(INetworkManager<E, N> networkManager, INetwork network) {
                addNetworkWatcher(network);
            }

            @Override
            public void onNetworkRemoved(INetworkManager<E, N> networkManager, INetwork network) {
                removeNetworkWatcher(network);
            }
        });
    }

    @Override
    public void onPlayerWatchChunk(EntityPlayerMP player, Chunk chunk) {
        for (INetwork network : intersector.getNetworksIntersectingChunk(manager, chunk.x, chunk.z)) {
            networkWatchers.get(network.getId()).addPlayer(player);
        }
    }

    @Override
    public void onPlayerUnWatchChunk(EntityPlayerMP player, Chunk chunk) {
        for (INetwork network : intersector.getNetworksIntersectingChunk(manager, chunk.x, chunk.z)) {
            networkWatchers.get(network.getId()).removePlayer(player);
        }
    }

    private NetworkWatcher addNetworkWatcher(INetwork network) {
        NetworkWatcher networkWatcher = new NetworkWatcher(network, networkChannel);
        network.addEventListener(LISTENER_KEY, networkWatcher);
        networkWatchers.put(network.getId(), networkWatcher);

        // Players may already be watching chunks that the network intersects with
        for (long position : intersector.getChunksIntersectingNetwork(network)) {
            int x = IIntersector.chunkX(position);
            int z = IIntersector.chunkZ(position);
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
        networkWatchers.remove(network.getUuid()).clear();
    }
}
