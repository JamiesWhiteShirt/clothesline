package com.jamieswhiteshirt.clothesline.common.network.message;

import com.jamieswhiteshirt.clothesline.common.util.BasicNetwork;
import com.jamieswhiteshirt.clothesline.common.util.ByteBufSerialization;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.*;

public class SetNetworksMessage implements IMessage {
    public List<BasicNetwork> networks;

    public SetNetworksMessage() {

    }

    public SetNetworksMessage(Collection<BasicNetwork> networks) {
        this.networks = new ArrayList<>(networks);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int numNetworks = buf.readUnsignedShort();
        BasicNetwork[] networks = new BasicNetwork[numNetworks];
        for (int i = 0; i < numNetworks; i++) {
            networks[i] = ByteBufSerialization.readNetwork(buf);
        }
        this.networks = Arrays.asList(networks);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeShort(networks.size());
        for (BasicNetwork network : networks) {
            ByteBufSerialization.writeNetwork(buf, network);
        }
    }
}
