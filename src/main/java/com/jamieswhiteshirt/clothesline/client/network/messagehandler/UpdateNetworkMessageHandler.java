package com.jamieswhiteshirt.clothesline.client.network.messagehandler;

import com.jamieswhiteshirt.clothesline.Clothesline;
import com.jamieswhiteshirt.clothesline.api.Network;
import com.jamieswhiteshirt.clothesline.api.client.IClientNetworkManager;
import com.jamieswhiteshirt.clothesline.common.network.message.UpdateNetworkMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nullable;

public class UpdateNetworkMessageHandler implements IMessageHandler<UpdateNetworkMessage, IMessage> {
    @Override
    @Nullable
    public IMessage onMessage(UpdateNetworkMessage message, MessageContext ctx) {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            WorldClient world = Minecraft.getMinecraft().world;
            if (world != null) {
                IClientNetworkManager manager = world.getCapability(Clothesline.CLIENT_NETWORK_MANAGER_CAPABILITY, null);
                if (manager != null) {
                    Network network = manager.getNetworkById(message.networkId);
                    if (network != null) {
                        network.getState().setShift(message.shift);
                        network.getState().setMomentum(message.momentum);
                    }
                }
            }
        });
        return null;
    }
}
