package com.jamieswhiteshirt.clothesline.client.network.messagehandler;

import com.jamieswhiteshirt.clothesline.Clothesline;
import com.jamieswhiteshirt.clothesline.api.IConnector;
import com.jamieswhiteshirt.clothesline.common.network.message.MessageSetConnectorPos;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

@SideOnly(Side.CLIENT)
public class MessageSetConnectorPosHandler implements IMessageHandler<MessageSetConnectorPos, IMessage> {
    @Override
    @Nullable
    public IMessage onMessage(MessageSetConnectorPos message, MessageContext ctx) {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            WorldClient world = Minecraft.getMinecraft().world;
            if (world != null) {
                Entity entity = world.getEntityByID(message.entityId);
                if (entity != null) {
                    IConnector connectionHolder = entity.getCapability(Clothesline.CONNECTOR_CAPABILITY, null);
                    if (connectionHolder != null) {
                        connectionHolder.setPos(message.fromPos);
                    }
                }
            }
        });
        return null;
    }
}
