package com.jamieswhiteshirt.clothesline.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;

import java.util.UUID;

/**
 * A clothesline network.
 *
 * A clothesline network has two constant IDs that uniquely identify the network within its {@link INetworkManager}.
 * {@link #getId()} returns the shorter ID of the clothesline network that may change whenever the clothesline network
 * is loaded from disk and may be reused. {@link #getUuid()} returns the longer UUID that will not change whenever the
 * clothesline network is loaded from disk and may not be reused.
 *
 * A clothesline network has an {@link INetworkState}.
 *
 * A clothesline network may have event listeners that will be notified when the state of the network changes or an
 * attached items changes. Event listeners are called in lexicographic order of their keys.
 */
public interface INetwork {
    /**
     * Returns the constant shorter ID of the clothesline network that uniquely identifies the clothesline network
     * within its {@link INetworkManager}. This ID may change whenever the clothesline network is loaded from disk and
     * may be reused.
     * @return the constant short ID of the network
     */
    int getId();

    /**
     * Returns the constant longer UUID of the clothesline network that uniquely identifies the clothesline network
     * within its {@link INetworkManager}. This ID will not change whenever the clothesline network is loaded from disk
     * and may not be reused.
     * @return the constant UUID of the network
     */
    UUID getUuid();

    /**
     * Returns the state of the clothesline network.
     * @return the state of the clothesline network
     */
    INetworkState getState();

    /**
     * Updates the state of the clothesline network, called each in-game tick.
     */
    void update();

    /**
     * Called when a player interacts with an item at a specified attachment key.
     * Returns true if the interaction should prevent other interactions triggering, otherwise false.
     * @param player the player
     * @param hand the hand the player is using
     * @param attachmentKey the attachment key
     * @return true if the interaction should prevent other interactions triggering, otherwise false
     */
    boolean useItem(EntityPlayer player, EnumHand hand, int attachmentKey);

    /**
     * Called when a player punches an attachment at the specified attachment key.
     * @param player the player
     * @param attachmentKey the attachment key
     */
    void hitAttachment(EntityPlayer player, int attachmentKey);

    /**
     * Insert an ItemStack in the slot of the specified attachment key. Each slot can hold only one item.
     *
     * If the stack in the slot changes and simulation is disabled, event listeners are notified with
     * {@link INetworkListener#onAttachmentChanged(INetwork, int, ItemStack, ItemStack)}
     * @see net.minecraftforge.items.IItemHandler#insertItem(int, ItemStack, boolean)
     */
    ItemStack insertItem(int attachmentKey, ItemStack stack, boolean simulate);

    /**
     * Extract the ItemStack in the slot of the specified attachment key. Each slot can hold only one item.
     *
     * If the stack in the slot changes and simulation is disabled, event listeners are notified with
     * {@link INetworkListener#onAttachmentChanged(INetwork, int, ItemStack, ItemStack)}
     * @see net.minecraftforge.items.IItemHandler#extractItem(int, int, boolean)
     */
    ItemStack extractItem(int attachmentKey, boolean simulate);

    /**
     * Get the ItemStack in the slot of the specified attachment key. The return value must not be mutated.
     * @see net.minecraftforge.items.IItemHandler#getStackInSlot(int)
     */
    ItemStack getAttachment(int attachmentKey);

    /**
     * Set the ItemStack in the slot of the specified attachment key, overriding anything already in the slot.
     *
     * Notifies event listeners with
     * {@link INetworkListener#onAttachmentChanged(INetwork, int, ItemStack, ItemStack)}
     */
    void setAttachment(int attachmentKey, ItemStack stack);

    /**
     * Adds an event listener that will be notified when the state of the clothesline network changes or when attached
     * items change. The event listener is bound by a key which must be unique for the clothesline network. If an
     * existing event listener is bound to the same key, it will be overridden.
     *
     * The event listener can be removed with {@link #removeEventListener(ResourceLocation)}.
     * @param key the event listener key
     * @param eventListener the event listener
     */
    void addEventListener(ResourceLocation key, INetworkListener eventListener);

    /**
     * Removes an event listener bound to the specified key with
     * {@link #addEventListener(ResourceLocation, INetworkListener)}. If no event listener is bound to the key,
     * nothing happens.
     * @param key the event listener key
     */
    void removeEventListener(ResourceLocation key);
}
