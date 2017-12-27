package com.jamieswhiteshirt.clothesline.common.network.message;

import com.jamieswhiteshirt.clothesline.common.util.ByteBufSerialization;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.UUID;

public class MessageTryUseItemOnNetwork implements IMessage {
    public EnumHand hand;
    public UUID networkUuid;
    public int offset;

    public MessageTryUseItemOnNetwork() {

    }

    public MessageTryUseItemOnNetwork(EnumHand hand, UUID networkUuid, int offset) {
        this.hand = hand;
        this.networkUuid = networkUuid;
        this.offset = offset;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.hand = EnumHand.values()[buf.readByte()];
        this.networkUuid = ByteBufSerialization.readNetworkUuid(buf);
        this.offset = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(hand.ordinal());
        ByteBufSerialization.writeNetworkUuid(buf, networkUuid);
        buf.writeInt(offset);
    }
}
