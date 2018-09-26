package com.jamieswhiteshirt.clothesline.client.network.messagehandler;

import com.jamieswhiteshirt.clothesline.Clothesline;
import com.jamieswhiteshirt.clothesline.api.client.IClientNetworkManager;
import com.jamieswhiteshirt.clothesline.common.network.message.SetNetworksMessage;
import com.jamieswhiteshirt.clothesline.common.util.BasicNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.stream.Collectors;

@SideOnly(Side.CLIENT)
public class SetNetworksMessageHandler implements IMessageHandler<SetNetworksMessage, IMessage> {
    @Override
    @Nullable
    public IMessage onMessage(SetNetworksMessage message, MessageContext ctx) {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            WorldClient world = ctx.getClientHandler().world;
			IClientNetworkManager manager = world.getCapability(Clothesline.CLIENT_NETWORK_MANAGER_CAPABILITY, null);
			if (manager != null) {
				manager.reset(message.networks.stream().map(
						BasicNetwork::toAbsolute
				).collect(Collectors.toList()));
			}
        });
        return null;
    }
}
