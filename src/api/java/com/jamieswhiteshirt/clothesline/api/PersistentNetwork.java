package com.jamieswhiteshirt.clothesline.api;

import java.util.UUID;

public final class PersistentNetwork {
    private final UUID uuid;
    private final INetworkState state;

    public PersistentNetwork(UUID uuid, INetworkState state) {
        this.uuid = uuid;
        this.state = state;
    }

    public UUID getUuid() {
        return uuid;
    }

    public INetworkState getState() {
        return state;
    }
}
