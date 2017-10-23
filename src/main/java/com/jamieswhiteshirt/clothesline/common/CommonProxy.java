package com.jamieswhiteshirt.clothesline.common;

import com.jamieswhiteshirt.clothesline.Clothesline;
import com.jamieswhiteshirt.clothesline.common.impl.NetworkManager;
import com.jamieswhiteshirt.clothesline.common.impl.SynchronizationListener;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public abstract class CommonProxy {
    public void preInit(FMLPreInitializationEvent event) {
    }

    public void init(FMLInitializationEvent event) {
    }

    public NetworkManager createNetworkManager(World world) {
        NetworkManager manager = new NetworkManager(world);
        if (world instanceof WorldServer) {
            manager.addEventListener(new SynchronizationListener((WorldServer) world, Clothesline.instance.networkWrapper));
        }
        return manager;
    }
}
