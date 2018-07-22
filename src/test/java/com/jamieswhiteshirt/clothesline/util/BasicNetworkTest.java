package com.jamieswhiteshirt.clothesline.util;

import com.jamieswhiteshirt.clothesline.common.impl.Network;
import com.jamieswhiteshirt.clothesline.common.util.BasicNetwork;
import org.junit.jupiter.api.Test;

class BasicNetworkTest {
    @Test
    void persistsEquivalency() {
        Network a = Networks.networkAB;
        BasicNetwork basicNetwork = BasicNetwork.fromAbsolute(a);
        Network b = basicNetwork.toAbsolute();
        Networks.assertNetworksEquivalent(a, b);
    }
}
