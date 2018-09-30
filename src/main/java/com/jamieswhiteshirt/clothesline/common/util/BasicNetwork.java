package com.jamieswhiteshirt.clothesline.common.util;

import com.jamieswhiteshirt.clothesline.api.INetwork;
import com.jamieswhiteshirt.clothesline.common.impl.Network;
import com.jamieswhiteshirt.clothesline.internal.PersistentNetwork;

import java.util.Objects;

public final class BasicNetwork {
    public static BasicNetwork fromAbsolute(INetwork network) {
        return new BasicNetwork(network.getId(), BasicPersistentNetwork.fromAbsolute(new PersistentNetwork(network.getUuid(), network.getState())));
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BasicNetwork that = (BasicNetwork) o;
        return id == that.id &&
            Objects.equals(persistentNetwork, that.persistentNetwork);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, persistentNetwork);
    }

    @Override
    public String toString() {
        return "BasicNetwork{" +
            "id=" + id +
            ", persistentNetwork=" + persistentNetwork +
            '}';
    }
}
