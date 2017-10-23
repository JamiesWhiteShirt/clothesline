package com.jamieswhiteshirt.clothesline.common.network.message;

import com.jamieswhiteshirt.clothesline.common.util.BasicAttachment;
import com.jamieswhiteshirt.clothesline.common.util.ByteBufSerialization;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.UUID;

public class MessageSetAttachment implements IMessage {
    public UUID networkUuid;
    public BasicAttachment attachment;

    public MessageSetAttachment() {

    }

    public MessageSetAttachment(UUID networkUuid, BasicAttachment attachment) {
        this.networkUuid = networkUuid;
        this.attachment = attachment;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.networkUuid = ByteBufSerialization.readNetworkUuid(buf);
        this.attachment = ByteBufSerialization.readAttachment(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufSerialization.writeNetworkUuid(buf, networkUuid);
        ByteBufSerialization.writeAttachment(buf, attachment);
    }
}
