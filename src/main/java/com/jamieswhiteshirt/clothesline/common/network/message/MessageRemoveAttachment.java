package com.jamieswhiteshirt.clothesline.common.network.message;

import com.jamieswhiteshirt.clothesline.common.util.ByteBufSerialization;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageRemoveAttachment implements IMessage {
    public int networkId;
    public int attachmentKey;

    public MessageRemoveAttachment() {

    }

    public MessageRemoveAttachment(int networkId, int attachmentKey) {
        this.networkId = networkId;
        this.attachmentKey = attachmentKey;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.networkId = ByteBufSerialization.readNetworkId(buf);
        this.attachmentKey = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufSerialization.writeNetworkId(buf, networkId);
        buf.writeInt(attachmentKey);
    }
}
