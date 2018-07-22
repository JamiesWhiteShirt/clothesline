package com.jamieswhiteshirt.clothesline.util;

import com.jamieswhiteshirt.clothesline.common.impl.Network;
import com.jamieswhiteshirt.clothesline.common.util.BasicNetwork;
import org.junit.jupiter.api.Test;

class BasicNetworkTest {
    @Test
    void persistsEquivalency() {
        Network a = NetworkTests.ab.network;
        BasicNetwork basicNetwork = BasicNetwork.fromAbsolute(a);
        Network b = basicNetwork.toAbsolute();
        NetworkTests.assertNetworksEquivalent(a, b);
    }
}
