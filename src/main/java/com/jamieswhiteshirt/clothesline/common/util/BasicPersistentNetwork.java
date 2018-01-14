package com.jamieswhiteshirt.clothesline.common.util;

import com.jamieswhiteshirt.clothesline.api.PersistentNetwork;

import java.util.UUID;

public class BasicPersistentNetwork {
    public static BasicPersistentNetwork fromAbsolute(PersistentNetwork network) {
        return new BasicPersistentNetwork(
                network.getUuid(),
                BasicNetworkState.fromAbsolute(network.getState())
        );
    }

    private final UUID uuid;
    private final BasicNetworkState state;

    public BasicPersistentNetwork(UUID uuid, BasicNetworkState state) {
        this.uuid = uuid;
        this.state = state;
    }

    public UUID getUuid() {
        return uuid;
    }

    public BasicNetworkState getState() {
        return state;
    }

    public PersistentNetwork toAbsolute() {
        return new PersistentNetwork(uuid, state.toAbsolute());
    }
}
