package com.jamieswhiteshirt.clothesline.common.network.message;

import com.jamieswhiteshirt.clothesline.common.NetworkUtil;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.UUID;

public class MessageRemoveAttachment implements IMessage {
    public UUID networkUuid;
    public int attachmentId;

    public MessageRemoveAttachment() {

    }

    public MessageRemoveAttachment(UUID networkUuid, int attachmentId) {
        this.networkUuid = networkUuid;
        this.attachmentId = attachmentId;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.networkUuid = NetworkUtil.readNetworkUuidFromByteBuf(buf);
        this.attachmentId = NetworkUtil.readAttachmentIdFromByteBuf(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        NetworkUtil.writeNetworkUuidToByteBuf(buf, networkUuid);
        NetworkUtil.writeAttachmentIdToByteBuf(buf, attachmentId);
    }
}
