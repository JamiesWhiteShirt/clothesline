package com.jamieswhiteshirt.clothesline.common.network.message;

import com.jamieswhiteshirt.clothesline.api.Network;
import com.jamieswhiteshirt.clothesline.common.NetworkUtil;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.*;
import java.util.stream.Collectors;

public class MessageSyncNetworks implements IMessage {
    public List<Network> networks;

    public MessageSyncNetworks() {

    }

    public MessageSyncNetworks(Map<UUID, Network> networks) {
        this.networks = networks.values().stream().collect(Collectors.toList());
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int numNetworks = buf.readUnsignedShort();
        Network[] networks = new Network[numNetworks];
        for (int i = 0; i < numNetworks; i++) {
            networks[i] = NetworkUtil.readNetworkFromByteBuf(buf);
        }
        this.networks = Arrays.asList(networks);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeShort(networks.size());
        for (Network network : networks) {
            NetworkUtil.writeNetworkToByteBuf(buf, network);
        }
    }
}
