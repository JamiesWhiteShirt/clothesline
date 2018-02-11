package com.jamieswhiteshirt.clothesline.common.network.message;

import com.jamieswhiteshirt.clothesline.common.util.BasicNetwork;
import com.jamieswhiteshirt.clothesline.common.util.ByteBufSerialization;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class AddNetworkMessage implements IMessage {
    public BasicNetwork network;

    public AddNetworkMessage() {

    }

    public AddNetworkMessage(BasicNetwork network) {
        this.network = network;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        network = ByteBufSerialization.readNetwork(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufSerialization.writeNetwork(buf, network);
    }
}
