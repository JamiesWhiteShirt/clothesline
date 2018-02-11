package com.jamieswhiteshirt.clothesline.api;

import java.util.UUID;

public final class PersistentNetwork {
    private final UUID uuid;
    private final AbsoluteNetworkState state;

    public PersistentNetwork(UUID uuid, AbsoluteNetworkState state) {
        this.uuid = uuid;
        this.state = state;
    }

    public UUID getUuid() {
        return uuid;
    }

    public AbsoluteNetworkState getState() {
        return state;
    }
}
