package com.jamieswhiteshirt.clothesline.api;

import java.util.UUID;

public final class Network {
    private final UUID uuid;
    private final int id;
    private AbsoluteNetworkState state;

    public Network(UUID uuid, int id, AbsoluteNetworkState state) {
        this.uuid = uuid;
        this.id = id;
        this.state = state;
    }

    public void setState(AbsoluteNetworkState state) {
        this.state = state;
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getId() {
        return id;
    }

    public AbsoluteNetworkState getState() {
        return state;
    }

    public void update() {
        this.state.update();
    }
}
