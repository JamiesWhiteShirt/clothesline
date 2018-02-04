package com.jamieswhiteshirt.clothesline.common.util;

import com.jamieswhiteshirt.clothesline.api.Network;

public class BasicNetwork {
    public static BasicNetwork fromAbsolute(Network network) {
        return new BasicNetwork(network.getId(), BasicPersistentNetwork.fromAbsolute(network.toPersistent()));
    }

    private final int id;
    private final BasicPersistentNetwork persistentNetwork;

    public BasicNetwork(int id, BasicPersistentNetwork persistentNetwork) {
        this.id = id;
        this.persistentNetwork = persistentNetwork;
    }

    public int getId() {
        return id;
    }

    public BasicPersistentNetwork getPersistentNetwork() {
        return persistentNetwork;
    }

    public Network toAbsolute() {
        return new Network(
                id,
                persistentNetwork.toAbsolute()
        );
    }
}
