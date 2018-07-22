package com.jamieswhiteshirt.clothesline.util;

import com.jamieswhiteshirt.clothesline.api.NetworkState;
import com.jamieswhiteshirt.clothesline.common.util.NetworkStateBuilder;
import org.junit.jupiter.api.Test;

class NetworkStateBuilderTest {
    @Test
    void persistsEquivalence() {
        NetworkState a = Networks.networkStateAB;
        NetworkStateBuilder networkStateBuilder = NetworkStateBuilder.fromAbsolute(a);
        NetworkState b = networkStateBuilder.toAbsolute();
        Networks.assertNetworkStatesEquivalent(a, b);
    }

    @Test
    void rerootABOnBEquivalentToBA() {
        NetworkState ab = Networks.networkStateAB;
        NetworkStateBuilder networkStateBuilder = NetworkStateBuilder.fromAbsolute(ab);
        networkStateBuilder.reroot(Networks.posB);
        NetworkState ba = networkStateBuilder.toAbsolute();
        Networks.assertNetworkStatesEquivalent(ba, Networks.networkStateBA);
    }
}
