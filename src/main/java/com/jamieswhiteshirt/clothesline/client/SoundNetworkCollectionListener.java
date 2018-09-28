package com.jamieswhiteshirt.clothesline.client;

import com.jamieswhiteshirt.clothesline.api.*;
import com.jamieswhiteshirt.clothesline.client.audio.ClotheslineRopeSound;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.*;

@SideOnly(Side.CLIENT)
public class SoundNetworkCollectionListener implements INetworkCollectionListener {
    private final Map<BlockPos, ClotheslineRopeSound> anchorSounds = new HashMap<>();
    private final SoundHandler soundHandler = Minecraft.getMinecraft().getSoundHandler();

    private void listenTo(INetworkState state) {
        for (Path.Node node : state.getPath().getNodes().values()) {
            ClotheslineRopeSound sound = new ClotheslineRopeSound(state, node);
            anchorSounds.put(node.getPos(), sound);
            soundHandler.playSound(sound);
        }
    }

    private void unlistenTo(INetworkState state) {
        for (BlockPos pos : state.getPath().getNodes().keySet()) {
            ClotheslineRopeSound sound = anchorSounds.remove(pos);
            if (sound != null) {
                soundHandler.stopSound(sound);
            }
        }
    }

    @Override
    public void onNetworkAdded(INetworkCollection networks, INetwork network) {
        listenTo(network.getState());
    }

    @Override
    public void onNetworkRemoved(INetworkCollection networks, INetwork network) {
        unlistenTo(network.getState());
    }
}
