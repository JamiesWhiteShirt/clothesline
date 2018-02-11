package com.jamieswhiteshirt.clothesline.common.network.message;

import com.jamieswhiteshirt.clothesline.common.util.ByteBufSerialization;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageTryUseItemOnNetwork implements IMessage {
    public EnumHand hand;
    public int networkId;
    public int attachmentKey;

    public MessageTryUseItemOnNetwork() {

    }

    public MessageTryUseItemOnNetwork(EnumHand hand, int networkId, int attachmentKey) {
        this.hand = hand;
        this.networkId = networkId;
        this.attachmentKey = attachmentKey;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.hand = EnumHand.values()[buf.readByte()];
        this.networkId = ByteBufSerialization.readNetworkId(buf);
        this.attachmentKey = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(hand.ordinal());
        ByteBufSerialization.writeNetworkId(buf, networkId);
        buf.writeInt(attachmentKey);
    }
}
