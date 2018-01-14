package com.jamieswhiteshirt.clothesline.api;

import java.util.UUID;

public final class Network {
    private final int id;
    private final PersistentNetwork persistentNetwork;

    public Network(int id, PersistentNetwork persistentNetwork) {
        this.id = id;
        this.persistentNetwork = persistentNetwork;
    }

    public int getId() {
        return id;
    }

    public UUID getUuid() {
        return persistentNetwork.getUuid();
    }

    public void setState(AbsoluteNetworkState state) {
        persistentNetwork.setState(state);
    }

    public AbsoluteNetworkState getState() {
        return persistentNetwork.getState();
    }

    public void update() {
        persistentNetwork.update();
    }

    public PersistentNetwork getPersistent() {
        return persistentNetwork;
    }
}
