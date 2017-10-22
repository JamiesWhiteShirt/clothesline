package com.jamieswhiteshirt.clothesline.client.network.messagehandler;

import com.jamieswhiteshirt.clothesline.Clothesline;
import com.jamieswhiteshirt.clothesline.api.INetworkManager;
import com.jamieswhiteshirt.clothesline.common.network.message.MessageSyncNetworks;
import com.jamieswhiteshirt.clothesline.common.util.BasicNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.Field;
import java.util.stream.Collectors;

@SideOnly(Side.CLIENT)
public class MessageSyncNetworksHandler implements IMessageHandler<MessageSyncNetworks, IMessage> {
    @CapabilityInject(INetworkManager.class)
    public static final Capability<INetworkManager> CLOTHESLINE_NETWORK_MANAGER_CAPABILITY = null;
    // This message may be received before the client world is actually assigned to Minecraft.
    // The network manager holds the world that will later be assigned to the client in this field.
    private static final Field clientWorldController = ReflectionHelper.findField(NetHandlerPlayClient.class, "field_147300_g", "clientWorldController");

    @Override
    public IMessage onMessage(MessageSyncNetworks message, MessageContext ctx) {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            WorldClient world = null;
            try {
                world = (WorldClient) clientWorldController.get(ctx.getClientHandler());
            } catch (IllegalAccessException e) {
                Clothesline.logger.error("Could not access client world for network sync", e);
            }
            if (world != null) {
                INetworkManager manager = world.getCapability(CLOTHESLINE_NETWORK_MANAGER_CAPABILITY, null);
                if (manager != null) {
                    manager.setNetworks(message.networks.stream().map(
                            BasicNetwork::toAbsolute
                    ).collect(Collectors.toList()));
                }
            }
        });
        return null;
    }
}
