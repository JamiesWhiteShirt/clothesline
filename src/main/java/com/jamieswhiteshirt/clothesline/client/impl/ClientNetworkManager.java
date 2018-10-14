package com.jamieswhiteshirt.clothesline.client.impl;

import com.jamieswhiteshirt.clothesline.api.*;
import com.jamieswhiteshirt.clothesline.common.impl.NetworkManager;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class ClientNetworkManager extends NetworkManager {
    public ClientNetworkManager(WorldClient world, INetworkCollection networks) {
        super(world, networks);
    }

    @Override
    protected void createNetwork(INetworkState networkState) {
    }

    @Override
    protected void deleteNetwork(INetwork network) {
    }

    @Override
    protected void dropItems(INetworkState state, boolean dropClotheslines) {
    }
}
