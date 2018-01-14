package com.jamieswhiteshirt.clothesline.client.impl;

import com.jamieswhiteshirt.clothesline.api.client.IClientNetworkManager;
import com.jamieswhiteshirt.clothesline.api.Network;
import com.jamieswhiteshirt.clothesline.common.impl.CommonNetworkManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@SideOnly(Side.CLIENT)
public final class ClientNetworkManager extends CommonNetworkManager implements IClientNetworkManager {
    @Override
    public void reset(List<Network> networks) {
        resetInternal(networks);
    }

    @Override
    public void addNetwork(Network network) {
        super.addNetwork(network);
    }

    @Override
    public void hitAttachment(Network network, EntityPlayer player, int attachmentKey) {
        setAttachment(network, attachmentKey, ItemStack.EMPTY);
    }
}
