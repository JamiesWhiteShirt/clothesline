package com.jamieswhiteshirt.clothesline.api;

import java.util.UUID;

public final class Network {
    private final int id;
    private final UUID uuid;
    private AbsoluteNetworkState state;

    public Network(int id, PersistentNetwork persistentNetwork) {
        this.id = id;
        this.uuid = persistentNetwork.getUuid();
        this.state = persistentNetwork.getState();
    }

    public int getId() {
        return id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setState(AbsoluteNetworkState state) {
        this.state = state;
    }

    public AbsoluteNetworkState getState() {
        return state;
    }

    public void update() {
        state.update();
    }

    public PersistentNetwork toPersistent() {
        return new PersistentNetwork(uuid, state);
    }
}
