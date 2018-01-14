package com.jamieswhiteshirt.clothesline.api;

import java.util.UUID;

public class PersistentNetwork {
    private final UUID uuid;
    private AbsoluteNetworkState state;

    public PersistentNetwork(UUID uuid, AbsoluteNetworkState state) {
        this.uuid = uuid;
        this.state = state;
    }

    public void setState(AbsoluteNetworkState state) {
        this.state = state;
    }

    public UUID getUuid() {
        return uuid;
    }

    public AbsoluteNetworkState getState() {
        return state;
    }

    public void update() {
        this.state.update();
    }
}
