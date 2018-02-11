package com.jamieswhiteshirt.clothesline.common.network.message;

import com.jamieswhiteshirt.clothesline.common.util.BasicNetworkState;
import com.jamieswhiteshirt.clothesline.common.util.ByteBufSerialization;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class SetNetworkStateMessage implements IMessage {
    public int networkId;
    public BasicNetworkState state;

    public SetNetworkStateMessage() {

    }

    public SetNetworkStateMessage(int networkId, BasicNetworkState state) {
        this.networkId = networkId;
        this.state = state;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        networkId = ByteBufSerialization.readNetworkId(buf);
        state = ByteBufSerialization.readNetworkState(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufSerialization.writeNetworkId(buf, networkId);
        ByteBufSerialization.writeNetworkState(buf, state);
    }
}
