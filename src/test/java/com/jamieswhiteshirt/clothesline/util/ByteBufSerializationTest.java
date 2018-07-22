package com.jamieswhiteshirt.clothesline.util;

import com.jamieswhiteshirt.clothesline.common.util.BasicNetwork;
import com.jamieswhiteshirt.clothesline.common.util.ByteBufSerialization;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ByteBufSerializationTest {
    @Test
    void networkSerializesCorrectly() {
        BasicNetwork written = BasicNetwork.fromAbsolute(Networks.networkAB);
        ByteBuf buf = Unpooled.buffer();
        ByteBufSerialization.writeNetwork(buf, written);
        BasicNetwork read = ByteBufSerialization.readNetwork(buf);
        Assertions.assertEquals(written, read);
    }
}
