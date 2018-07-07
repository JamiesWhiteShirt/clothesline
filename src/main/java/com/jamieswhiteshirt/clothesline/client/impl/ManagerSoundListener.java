package com.jamieswhiteshirt.clothesline.client.impl;

import com.jamieswhiteshirt.clothesline.api.*;
import com.jamieswhiteshirt.clothesline.client.audio.ClotheslineRopeSound;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.*;
import java.util.stream.Collectors;

@SideOnly(Side.CLIENT)
public class ManagerSoundListener<T extends INetworkEdge> implements INetworkManagerEventListener<T> {
    private static final ResourceLocation SOUND_KEY = new ResourceLocation("clothesline", "sound");

    private final Map<BlockPos, ClotheslineRopeSound> anchorSounds = new HashMap<>();
    private final SoundHandler soundHandler = Minecraft.getMinecraft().getSoundHandler();
    private final INetworkEventListener networkSoundListener = new INetworkEventListener() {
        @Override
        public void onStateChanged(INetwork network, AbsoluteNetworkState previousState, AbsoluteNetworkState newState) {
            unlistenTo(previousState);
            listenTo(newState);
        }

        @Override
        public void onAttachmentChanged(INetwork network, int attachmentKey, ItemStack previousStack, ItemStack newStack) {
        }
    };

    private void listenTo(AbsoluteNetworkState state) {
        for (Graph.Node node : state.getGraph().getNodes()) {
            ClotheslineRopeSound sound = new ClotheslineRopeSound(state, node);
            anchorSounds.put(node.getKey(), sound);
            soundHandler.playSound(sound);
        }
    }

    private void unlistenTo(AbsoluteNetworkState state) {
        for (Graph.Node node : state.getGraph().getNodes()) {
            ClotheslineRopeSound sound = anchorSounds.remove(node.getKey());
            if (sound != null) {
                soundHandler.stopSound(sound);
            }
        }
    }

    private void listenTo(INetwork network) {
        listenTo(network.getState());
        network.addEventListener(SOUND_KEY, networkSoundListener);
    }

    private void unlistenTo(INetwork network) {
        unlistenTo(network.getState());
    }

    @Override
    public void onNetworksReset(INetworkManager<T> networkManager, List<INetwork> previousNetworks, List<INetwork> newNetworks) {
        previousNetworks.forEach(this::unlistenTo);
        newNetworks.forEach(this::listenTo);
    }

    @Override
    public void onNetworkAdded(INetworkManager<T> networkManager, INetwork network) {
        listenTo(network);
    }

    @Override
    public void onNetworkRemoved(INetworkManager<T> networkManager, INetwork network) {
        unlistenTo(network);
    }

    @Override
    public void onUpdate(INetworkManager<T> networkManager) {
        /* EntityPlayerSP player = Minecraft.getMinecraft().player;
        int minX = MathHelper.floor(player.posX - 10.0D);
        int minY = MathHelper.floor(player.posY + player.getEyeHeight() - 10.0D);
        int minZ = MathHelper.floor(player.posZ - 10.0D);
        int maxX = MathHelper.floor(player.posX + 10.0D);
        int maxY = MathHelper.floor(player.posY + player.getEyeHeight() + 10.0D);
        int maxZ = MathHelper.floor(player.posZ + 10.0D);
        List<INetworkNode> networkNodes = networkManager.getNetworkNodes().values().stream().filter(it -> {
            BlockPos pos = it.getGraphNode().getKey();
            int x = pos.getX();
            int y = pos.getY();
            int z = pos.getZ();
            return x >= minX && y >= minY && z >= minZ && x < maxX && y < maxY && z < maxZ;
        }).collect(Collectors.toList());

        for (INetworkNode networkNode : networkNodes) {
        } */
    }
}
