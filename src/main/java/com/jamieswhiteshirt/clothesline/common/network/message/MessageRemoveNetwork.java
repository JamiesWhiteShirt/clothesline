package com.jamieswhiteshirt.clothesline.common.network.message;

import com.jamieswhiteshirt.clothesline.common.util.ByteBufSerialization;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.UUID;

public class MessageRemoveNetwork implements IMessage {
    public UUID networkUuid;

    public MessageRemoveNetwork() {

    }

    public MessageRemoveNetwork(UUID networkUuid) {
        this.networkUuid = networkUuid;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        networkUuid = ByteBufSerialization.readNetworkUuid(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufSerialization.writeNetworkUuid(buf, networkUuid);
    }
}
