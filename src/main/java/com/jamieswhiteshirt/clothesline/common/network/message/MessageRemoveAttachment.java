package com.jamieswhiteshirt.clothesline.common.network.message;

import com.jamieswhiteshirt.clothesline.common.util.ByteBufSerialization;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.UUID;

public class MessageRemoveAttachment implements IMessage {
    public UUID networkUuid;
    public int attachmentKey;

    public MessageRemoveAttachment() {

    }

    public MessageRemoveAttachment(UUID networkUuid, int attachmentKey) {
        this.networkUuid = networkUuid;
        this.attachmentKey = attachmentKey;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.networkUuid = ByteBufSerialization.readNetworkUuid(buf);
        this.attachmentKey = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufSerialization.writeNetworkUuid(buf, networkUuid);
        buf.writeInt(attachmentKey);
    }
}
