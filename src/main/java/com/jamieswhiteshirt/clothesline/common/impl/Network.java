package com.jamieswhiteshirt.clothesline.common.impl;

import com.jamieswhiteshirt.clothesline.api.*;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.*;

public final class Network implements INetwork {
    private final int id;
    private final UUID uuid;
    private INetworkState state;
    private final Map<ResourceLocation, INetworkListener> eventListeners = new TreeMap<>();

    public Network(int id, PersistentNetwork persistentNetwork) {
        this.id = id;
        this.uuid = persistentNetwork.getUuid();
        this.state = persistentNetwork.getState();
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public INetworkState getState() {
        return state;
    }

    @Override
    public void update() {
        state.update();
    }

    @Override
    public boolean useItem(EntityPlayer player, EnumHand hand, int attachmentKey) {
        ItemStack stack = player.getHeldItem(hand);
        if (!stack.isEmpty()) {
            if (state.getAttachment(attachmentKey).isEmpty()) {
                player.setHeldItem(hand, insertItem(attachmentKey, stack, false));
                return true;
            }
        }
        return false;
    }

    @Override
    public void hitAttachment(EntityPlayer player, int attachmentKey) {
        ItemStack stack = state.getAttachment(attachmentKey);
        if (!stack.isEmpty()) {
            setAttachment(attachmentKey, ItemStack.EMPTY);
            World world = player.world;
            if (!world.isRemote && world.getGameRules().getBoolean("doTileDrops")) {
                Vec3d pos = state.getPath().getPositionForOffset(state.attachmentKeyToOffset(attachmentKey));
                EntityItem entityitem = new EntityItem(world, pos.x, pos.y - 0.5D, pos.z, stack);
                entityitem.setDefaultPickupDelay();
                world.spawnEntity(entityitem);
            }
        }
    }

    @Override
    public ItemStack insertItem(int attachmentKey, ItemStack stack, boolean simulate) {
        if (!stack.isEmpty() && state.getAttachment(attachmentKey).isEmpty()) {
            if (!simulate) {
                ItemStack insertedItem = stack.copy();
                insertedItem.setCount(1);
                setAttachment(attachmentKey, insertedItem);
            }

            ItemStack returnedStack = stack.copy();
            returnedStack.shrink(1);
            return returnedStack;
        }
        return stack;
    }

    @Override
    public ItemStack extractItem(int attachmentKey, boolean simulate) {
        ItemStack result = state.getAttachment(attachmentKey);
        if (!result.isEmpty() && !simulate) {
            setAttachment(attachmentKey, ItemStack.EMPTY);
        }
        return result;
    }

    @Override
    public ItemStack getAttachment(int attachmentKey) {
        return state.getAttachment(attachmentKey);
    }

    @Override
    public void setAttachment(int attachmentKey, ItemStack stack) {
        ItemStack previousStack = state.getAttachment(attachmentKey);
        state.setAttachment(attachmentKey, stack);

        for (INetworkListener eventListener : eventListeners.values()) {
            eventListener.onAttachmentChanged(this, attachmentKey, previousStack, stack);
        }
    }

    @Override
    public void addEventListener(ResourceLocation key, INetworkListener eventListener) {
        eventListeners.put(key, eventListener);
    }

    @Override
    public void removeEventListener(ResourceLocation key) {
        eventListeners.remove(key);
    }
}
