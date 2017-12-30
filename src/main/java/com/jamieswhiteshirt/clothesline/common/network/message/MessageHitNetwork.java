package com.jamieswhiteshirt.clothesline.common.network.message;

import com.jamieswhiteshirt.clothesline.common.util.ByteBufSerialization;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.UUID;

public class MessageHitNetwork implements IMessage {
    public UUID networkUuid;
    public int attachmentKey;
    public int offset;

    public MessageHitNetwork() { }

    public MessageHitNetwork(UUID networkUuid, int attachmentKey, int offset) {
        this.networkUuid = networkUuid;
        this.attachmentKey = attachmentKey;
        this.offset = offset;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        networkUuid = ByteBufSerialization.readNetworkUuid(buf);
        attachmentKey = buf.readInt();
        offset = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufSerialization.writeNetworkUuid(buf, networkUuid);
        buf.writeInt(attachmentKey);
        buf.writeInt(offset);
    }
}
