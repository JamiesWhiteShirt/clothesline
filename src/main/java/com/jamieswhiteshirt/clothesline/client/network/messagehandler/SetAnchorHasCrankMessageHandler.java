package com.jamieswhiteshirt.clothesline.client.network.messagehandler;

import com.jamieswhiteshirt.clothesline.common.network.message.SetAnchorHasCrankMessage;
import com.jamieswhiteshirt.clothesline.common.tileentity.TileEntityClotheslineAnchor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

@SideOnly(Side.CLIENT)
public class SetAnchorHasCrankMessageHandler implements IMessageHandler<SetAnchorHasCrankMessage, IMessage> {
    @Override
    @Nullable
    public IMessage onMessage(SetAnchorHasCrankMessage message, MessageContext ctx) {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            WorldClient world = Minecraft.getMinecraft().world;
            if (world != null) {
                TileEntity tileEntity = world.getTileEntity(message.pos);
                if (tileEntity instanceof TileEntityClotheslineAnchor) {
                    ((TileEntityClotheslineAnchor) tileEntity).setHasCrank(message.hasCrank);
                }
            }
        });
        return null;
    }
}
