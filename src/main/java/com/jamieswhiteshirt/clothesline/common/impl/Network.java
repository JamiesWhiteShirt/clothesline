package com.jamieswhiteshirt.clothesline.common.impl;

import com.jamieswhiteshirt.clothesline.api.AbsoluteNetworkState;
import com.jamieswhiteshirt.clothesline.api.INetwork;
import com.jamieswhiteshirt.clothesline.api.INetworkEventListener;
import com.jamieswhiteshirt.clothesline.api.PersistentNetwork;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.*;

public final class Network implements INetwork {
    private final int id;
    private final UUID uuid;
    private AbsoluteNetworkState state;
    private final Map<ResourceLocation, INetworkEventListener> eventListeners = new TreeMap<>();

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
    public AbsoluteNetworkState getState() {
        return state;
    }

    @Override
    public void setState(AbsoluteNetworkState state) {
        AbsoluteNetworkState previousState = this.state;
        this.state = state;

        for (INetworkEventListener eventListener : eventListeners.values()) {
            eventListener.onStateChanged(previousState, state);
        }
    }

    @Override
    public void update() {
        state.update();
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
    public void setAttachment(int attachmentKey, ItemStack stack) {
        ItemStack previousStack = state.getAttachment(attachmentKey);
        state.setAttachment(attachmentKey, stack);

        for (INetworkEventListener eventListener : eventListeners.values()) {
            eventListener.onAttachmentChanged(attachmentKey, previousStack, stack);
        }
    }

    @Override
    public void addEventListener(ResourceLocation key, INetworkEventListener eventListener) {
        eventListeners.put(key, eventListener);
    }

    @Override
    public void removeEventListener(ResourceLocation key) {
        eventListeners.remove(key);
    }
}
