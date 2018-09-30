package com.jamieswhiteshirt.clothesline.common.util;

import com.jamieswhiteshirt.clothesline.internal.PersistentNetwork;

import java.util.Objects;
import java.util.UUID;

public final class BasicPersistentNetwork {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BasicPersistentNetwork that = (BasicPersistentNetwork) o;
        return Objects.equals(uuid, that.uuid) &&
            Objects.equals(state, that.state);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, state);
    }

    @Override
    public String toString() {
        return "BasicPersistentNetwork{" +
            "uuid=" + uuid +
            ", state=" + state +
            '}';
    }
}
