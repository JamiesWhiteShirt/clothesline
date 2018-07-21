package com.jamieswhiteshirt.clothesline.api;

import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;

public class NetworkManagerCreatedEvent extends Event {
    private final World world;
    private final INetworkManager<?, ?> networkManager;

    public NetworkManagerCreatedEvent(World world, INetworkManager<?, ?> networkManager) {
        this.world = world;
        this.networkManager = networkManager;
    }

    public World getWorld() {
        return world;
    }

    public INetworkManager<?, ?> getNetworkManager() {
        return networkManager;
    }
}
