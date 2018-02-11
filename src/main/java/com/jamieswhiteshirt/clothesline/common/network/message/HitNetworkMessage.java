package com.jamieswhiteshirt.clothesline.common.network.message;

import com.jamieswhiteshirt.clothesline.common.util.ByteBufSerialization;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class HitNetworkMessage implements IMessage {
    public int networkId;
    public int attachmentKey;
    public int offset;

    public HitNetworkMessage() { }

    public HitNetworkMessage(int networkId, int attachmentKey, int offset) {
        this.networkId = networkId;
        this.attachmentKey = attachmentKey;
        this.offset = offset;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        networkId = ByteBufSerialization.readNetworkId(buf);
        attachmentKey = buf.readInt();
        offset = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufSerialization.writeNetworkId(buf, networkId);
        buf.writeInt(attachmentKey);
        buf.writeInt(offset);
    }
}
