package com.jamieswhiteshirt.clothesline.api.client;

import com.jamieswhiteshirt.clothesline.api.ICommonNetworkManager;
import com.jamieswhiteshirt.clothesline.api.Network;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@SideOnly(Side.CLIENT)
public interface IClientNetworkManager extends ICommonNetworkManager {
    void addNetwork(Network network);

    void reset(List<Network> networks);
}
