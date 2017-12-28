package com.jamieswhiteshirt.clothesline.common.network.message;

import com.jamieswhiteshirt.clothesline.common.util.ByteBufSerialization;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.UUID;

public class MessageHitAttachment implements IMessage {
    public UUID networkUuid;
    public int offset;

    public MessageHitAttachment() { }

    public MessageHitAttachment(UUID networkUuid, int offset) {
        this.networkUuid = networkUuid;
        this.offset = offset;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.networkUuid = ByteBufSerialization.readNetworkUuid(buf);
        this.offset = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufSerialization.writeNetworkUuid(buf, networkUuid);
        buf.writeInt(offset);
    }
}
