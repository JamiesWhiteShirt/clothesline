package com.jamieswhiteshirt.clothesline.common.network.message;

import com.jamieswhiteshirt.clothesline.api.Network;
import com.jamieswhiteshirt.clothesline.common.NetworkUtil;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageAddNetwork implements IMessage {
    public Network network;

    public MessageAddNetwork() {

    }

    public MessageAddNetwork(Network network) {
        this.network = network;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        network = NetworkUtil.readNetworkFromByteBuf(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        NetworkUtil.writeNetworkToByteBuf(buf, network);
    }
}
