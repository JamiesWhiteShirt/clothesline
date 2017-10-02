package com.jamieswhiteshirt.clothesline.client.network.messagehandler;

import com.jamieswhiteshirt.clothesline.api.INetworkManager;
import com.jamieswhiteshirt.clothesline.api.Network;
import com.jamieswhiteshirt.clothesline.common.network.message.MessageSyncNetworks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.stream.Collectors;

@SideOnly(Side.CLIENT)
public class MessageSyncNetworksHandler implements IMessageHandler<MessageSyncNetworks, IMessage> {
    @CapabilityInject(INetworkManager.class)
    public static Capability<INetworkManager> CLOTHESLINE_NETWORK_MANAGER_CAPABILITY;

    @Override
    public IMessage onMessage(MessageSyncNetworks message, MessageContext ctx) {
        WorldClient world = Minecraft.getMinecraft().world;
        if (world != null) {
            INetworkManager manager = world.getCapability(CLOTHESLINE_NETWORK_MANAGER_CAPABILITY, null);
            if (manager != null) {
                manager.setNetworks(message.networks.stream().collect(Collectors.toMap(
                        Network::getUuid,
                        network -> network
                )));
            }
        }
        return null;
    }
}
