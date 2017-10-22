package com.jamieswhiteshirt.clothesline.common.network.message;

import com.jamieswhiteshirt.clothesline.common.util.ByteBufSerialization;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.UUID;

public class MessageRemoveItem implements IMessage {
    public UUID networkUuid;
    public int offset;

    public MessageRemoveItem() {

    }

    public MessageRemoveItem(UUID networkUuid, int offset) {
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
