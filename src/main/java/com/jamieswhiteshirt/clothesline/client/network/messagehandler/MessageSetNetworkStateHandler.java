package com.jamieswhiteshirt.clothesline.client.network.messagehandler;

import com.jamieswhiteshirt.clothesline.api.INetworkManager;
import com.jamieswhiteshirt.clothesline.api.Network;
import com.jamieswhiteshirt.clothesline.common.Util;
import com.jamieswhiteshirt.clothesline.common.network.message.MessageSetNetworkState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageSetNetworkStateHandler implements IMessageHandler<MessageSetNetworkState, IMessage> {
    @CapabilityInject(INetworkManager.class)
    private static final Capability<INetworkManager> CLOTHESLINE_NETWORK_MANAGER_CAPABILITY = Util.nonNullInjected();

    @Override
    public IMessage onMessage(MessageSetNetworkState message, MessageContext ctx) {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            WorldClient world = Minecraft.getMinecraft().world;
            if (world != null) {
                INetworkManager manager = world.getCapability(CLOTHESLINE_NETWORK_MANAGER_CAPABILITY, null);
                if (manager != null) {
                    Network network = manager.getNetworkByUUID(message.networkUuid);
                    if (network != null) {
                        manager.setNetworkState(network, message.state.toAbsolute());
                    }
                }
            }
        });
        return null;
    }
}
