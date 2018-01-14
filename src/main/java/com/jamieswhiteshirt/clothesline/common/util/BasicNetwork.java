package com.jamieswhiteshirt.clothesline.common.util;

import com.jamieswhiteshirt.clothesline.api.Network;

import java.util.UUID;

public class BasicNetwork {
    public static BasicNetwork fromAbsolute(Network network) {
        return new BasicNetwork(
                network.getUuid(),
                BasicNetworkState.fromAbsolute(network.getState())
        );
    }

    private final UUID uuid;
    private final BasicNetworkState state;

    public BasicNetwork(UUID uuid, BasicNetworkState state) {
        this.uuid = uuid;
        this.state = state;
    }

    public UUID getUuid() {
        return uuid;
    }

    public BasicNetworkState getState() {
        return state;
    }

    public Network toAbsolute(int id) {
        return new Network(
                uuid,
                id,
                state.toAbsolute()
        );
    }
}
