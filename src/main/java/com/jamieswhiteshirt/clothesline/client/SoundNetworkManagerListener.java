package com.jamieswhiteshirt.clothesline.client;

import com.jamieswhiteshirt.clothesline.api.*;
import com.jamieswhiteshirt.clothesline.client.audio.ClotheslineRopeSound;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.*;

@SideOnly(Side.CLIENT)
public class SoundNetworkManagerListener<E extends INetworkEdge, N extends INetworkNode> implements INetworkManagerEventListener<E, N> {
    private static final ResourceLocation SOUND_KEY = new ResourceLocation("clothesline", "sound");

    private final Map<BlockPos, ClotheslineRopeSound> anchorSounds = new HashMap<>();
    private final SoundHandler soundHandler = Minecraft.getMinecraft().getSoundHandler();
    private final INetworkEventListener networkSoundListener = new INetworkEventListener() {
        @Override
        public void onStateChanged(INetwork network, NetworkState previousState, NetworkState newState) {
            unlistenTo(previousState);
            listenTo(newState);
        }

        @Override
        public void onAttachmentChanged(INetwork network, int attachmentKey, ItemStack previousStack, ItemStack newStack) {
        }
    };

    private void listenTo(NetworkState state) {
        for (Graph.Node node : state.getGraph().getNodes().values()) {
            ClotheslineRopeSound sound = new ClotheslineRopeSound(state, node);
            anchorSounds.put(node.getKey(), sound);
            soundHandler.playSound(sound);
        }
    }

    private void unlistenTo(NetworkState state) {
        for (BlockPos pos : state.getGraph().getNodes().keySet()) {
            ClotheslineRopeSound sound = anchorSounds.remove(pos);
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
    public void onNetworksReset(INetworkManager<E, N> networkManager, List<INetwork> previousNetworks, List<INetwork> newNetworks) {
        previousNetworks.forEach(this::unlistenTo);
        newNetworks.forEach(this::listenTo);
    }

    @Override
    public void onNetworkAdded(INetworkManager<E, N> networkManager, INetwork network) {
        listenTo(network);
    }

    @Override
    public void onNetworkRemoved(INetworkManager<E, N> networkManager, INetwork network) {
        unlistenTo(network);
    }

    @Override
    public void onUpdate(INetworkManager<E, N> networkManager) {
        /* EntityPlayerSP player = Minecraft.getMinecraft().player;
        Box box = Box.create(
            MathHelper.floor(player.posX - 10.0D),
            MathHelper.floor(player.posY + player.getEyeHeight() - 10.0D),
            MathHelper.floor(player.posZ - 10.0D),
            MathHelper.ceil(player.posX + 10.0D),
            MathHelper.ceil(player.posY + player.getEyeHeight() + 10.0D),
            MathHelper.ceil(player.posZ + 10.0D)
        );
        networkManager.getNodes().entries(box::intersects).forEach(node -> {
        }); */
    }
}
