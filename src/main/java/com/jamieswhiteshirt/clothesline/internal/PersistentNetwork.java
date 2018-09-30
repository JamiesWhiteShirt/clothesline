package com.jamieswhiteshirt.clothesline.internal;

import com.jamieswhiteshirt.clothesline.api.INetworkState;

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
