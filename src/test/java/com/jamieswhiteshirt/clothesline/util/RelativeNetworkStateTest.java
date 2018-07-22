package com.jamieswhiteshirt.clothesline.util;

import com.jamieswhiteshirt.clothesline.api.AbsoluteNetworkState;
import com.jamieswhiteshirt.clothesline.common.util.RelativeNetworkState;
import org.junit.jupiter.api.Test;

class RelativeNetworkStateTest {
    @Test
    void persistsEquivalence() {
        AbsoluteNetworkState a = Networks.absoluteNetworkStateAB;
        RelativeNetworkState relativeNetworkState = RelativeNetworkState.fromAbsolute(a);
        AbsoluteNetworkState b = relativeNetworkState.toAbsolute();
        Networks.assertNetworkStatesEquivalent(a, b);
    }

    @Test
    void rerootABOnBEquivalentToBA() {
        AbsoluteNetworkState ab = Networks.absoluteNetworkStateAB;
        RelativeNetworkState relativeNetworkState = RelativeNetworkState.fromAbsolute(ab);
        relativeNetworkState.reroot(Networks.posB);
        AbsoluteNetworkState ba = relativeNetworkState.toAbsolute();
        Networks.assertNetworkStatesEquivalent(ba, Networks.absoluteNetworkStateBA);
    }
}
