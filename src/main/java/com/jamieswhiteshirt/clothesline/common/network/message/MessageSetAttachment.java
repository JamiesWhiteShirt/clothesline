package com.jamieswhiteshirt.clothesline.common.network.message;

import com.jamieswhiteshirt.clothesline.common.util.BasicAttachment;
import com.jamieswhiteshirt.clothesline.common.util.ByteBufSerialization;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageSetAttachment implements IMessage {
    public int networkId;
    public BasicAttachment attachment;

    public MessageSetAttachment() {

    }

    public MessageSetAttachment(int networkId, BasicAttachment attachment) {
        this.networkId = networkId;
        this.attachment = attachment;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.networkId = ByteBufSerialization.readNetworkId(buf);
        this.attachment = ByteBufSerialization.readAttachment(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufSerialization.writeNetworkId(buf, networkId);
        ByteBufSerialization.writeAttachment(buf, attachment);
    }
}
