package com.jamieswhiteshirt.clothesline.common.network.message;

import com.jamieswhiteshirt.clothesline.api.Attachment;
import com.jamieswhiteshirt.clothesline.common.NetworkUtil;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.UUID;

public class MessageAddAttachment implements IMessage {
    public UUID networkUuid;
    public Attachment attachment;

    public MessageAddAttachment() {

    }

    public MessageAddAttachment(UUID networkUuid, Attachment attachment) {
        this.networkUuid = networkUuid;
        this.attachment = attachment;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.networkUuid = NetworkUtil.readNetworkUuidFromByteBuf(buf);
        this.attachment = NetworkUtil.readAttachmentFromByteBuf(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        NetworkUtil.writeNetworkUuidToByteBuf(buf, networkUuid);
        NetworkUtil.writeAttachmentToByteBuf(buf, attachment);
    }
}
