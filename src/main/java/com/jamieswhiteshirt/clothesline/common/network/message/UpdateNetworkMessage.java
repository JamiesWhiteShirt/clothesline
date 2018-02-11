package com.jamieswhiteshirt.clothesline.common.network.message;

import com.jamieswhiteshirt.clothesline.common.util.ByteBufSerialization;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class UpdateNetworkMessage implements IMessage {
    public int networkId;
    public int shift;
    public int momentum;

    public UpdateNetworkMessage(int networkId, int shift, int momentum) {
        this.networkId = networkId;
        this.shift = shift;
        this.momentum = momentum;
    }

    public UpdateNetworkMessage() { }

    @Override
    public void fromBytes(ByteBuf buf) {
        networkId = ByteBufSerialization.readNetworkId(buf);
        shift = buf.readInt();
        momentum = buf.readUnsignedByte();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufSerialization.writeNetworkId(buf, networkId);
        buf.writeInt(shift);
        buf.writeByte(momentum);
    }
}
