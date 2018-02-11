package com.jamieswhiteshirt.clothesline.common.network.message;

import com.jamieswhiteshirt.clothesline.common.util.ByteBufSerialization;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class RemoveNetworkMessage implements IMessage {
    public int networkId;

    public RemoveNetworkMessage() {

    }

    public RemoveNetworkMessage(int networkId) {
        this.networkId = networkId;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        networkId = ByteBufSerialization.readNetworkId(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufSerialization.writeNetworkId(buf, networkId);
    }
}
