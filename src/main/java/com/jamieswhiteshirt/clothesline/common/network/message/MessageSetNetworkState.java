package com.jamieswhiteshirt.clothesline.common.network.message;

import com.jamieswhiteshirt.clothesline.common.util.BasicNetworkState;
import com.jamieswhiteshirt.clothesline.common.util.ByteBufSerialization;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.UUID;

public class MessageSetNetworkState implements IMessage {
    public UUID networkUuid;
    public BasicNetworkState state;

    public MessageSetNetworkState() {

    }

    public MessageSetNetworkState(UUID networkUuid, BasicNetworkState state) {
        this.networkUuid = networkUuid;
        this.state = state;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        networkUuid = ByteBufSerialization.readNetworkUuid(buf);
        state = ByteBufSerialization.readNetworkState(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufSerialization.writeNetworkUuid(buf, networkUuid);
        ByteBufSerialization.writeNetworkState(buf, state);
    }
}
