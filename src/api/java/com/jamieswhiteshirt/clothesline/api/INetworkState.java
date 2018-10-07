package com.jamieswhiteshirt.clothesline.api;

import com.jamieswhiteshirt.clothesline.api.util.MutableSortedIntMap;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * The state of an {@link INetwork}.
 *
 * The state contains a structure, a rotational offset, a rotational momentum and a range of attachment slots.
 */
public interface INetworkState {
    /**
     * The maximum amount of attachment units per tick that the clothesline network may rotate with.
     * @see AttachmentUnit
     */
    int MAX_MOMENTUM = 40;

    /**
     * Returns the tree, a canonical representation of the structure of the clothesline network.
     * @return the tree
     */
    Tree getTree();

    /**
     * Returns the path through the structure of the clothesline network. This structure is not canonical, it derived
     * from the tree.
     * @return the path through the structure of the clothesline network
     */
    Path getPath();

    /**
     * Returns the span of chunks covered by the clothesline network. Values are chunk positions encoded with
     * {@link net.minecraft.util.math.ChunkPos#asLong(int, int)}. This set must not be modified.
     * @return the span of chunks covered by the clothesline network
     */
    LongSet getChunkSpan();

    /**
     * Returns the range of attachment slots, on [0, {@link #getPathLength()})
     * @return the range of attachment slots
     */
    MutableSortedIntMap<ItemStack> getAttachments();

    /**
     * Returns a list of nonempty attachments in the specified range. It will "wrap around" {@link #getPathLength()}.
     * @param minAttachmentKey the min attachment key, inclusive
     * @param maxAttachmentKey the max attachment key, inclusive
     * @return a list of nonempty attachments in the specified range
     */
    List<MutableSortedIntMap.Entry<ItemStack>> getAttachmentsInRange(int minAttachmentKey, int maxAttachmentKey);

    /**
     * Get the attached ItemStack in the specified attachment slot.
     * @param attachmentKey the attachment slot
     * @return the attached ItemStack in the specified attachment slot
     */
    ItemStack getAttachment(int attachmentKey);

    /**
     * Set the attached ItemStack in the specified attachment slot.
     * @param attachmentKey the attachment slot
     * @param stack the attached ItemStack
     */
    void setAttachment(int attachmentKey, ItemStack stack);

    /**
     * Updates the network state, called each in-game tick.
     */
    void update();

    /**
     * Returns the current rotational offset of the clothesline network in attachment units per tick.
     * @return the current rotational offset of the clothesline network in attachment units per tick
     */
    int getShift();

    /**
     * Sets the current rotational offset of the clothesline network in attachment units.
     * @param shift the current rotational offset of the clothesline network in attachment units
     */
    void setShift(int shift);

    /**
     * Returns the previous rotational offset of the clothesline network in attachment units.
     * @return the previous rotational offset of the clothesline network in attachment units
     */
    int getPreviousShift();

    /**
     * Returns the interpolated rotational offset of the clothesline network in attachment units.
     * @param partialTicks the interpolation parameter
     * @return the interpolated rotational offset of the clothesline network in attachment units
     */
    double getShift(float partialTicks);

    /**
     * Returns the current rotational momentum of the clothesline network in attachment units per tick.
     * @return the current rotational momentum of the clothesline network in attachment units per tick
     */
    int getMomentum();

    /**
     * Sets the current rotational momentum of the clothesline network in attachment units per tick.
     * @param momentum the current rotational momentum of the clothesline network in attachment units per tick
     */
    void setMomentum(int momentum);

    /**
     * Returns the previous rotational momentum of the clothesline network in attachment units per tick.
     * @return the previous rotational momentum of the clothesline network in attachment units per tick
     */
    int getPreviousMomentum();

    /**
     * Returns the interpolated rotational momentum of the clothesline network in attachment units per tick.
     * @param partialTicks the interpolation parameter
     * @return the interpolated rotational momentum of the clothesline network in attachment units per tick
     */
    double getMomentum(float partialTicks);

    /**
     * Returns the length of the path in attachment units.
     * @return the length of the path in attachment units
     */
    int getPathLength();

    /**
     * Returns the attachment slot of the traversal position based on the current shift of the clothesline network.
     * @param offset the traversal position
     * @return the attachment slot of the traversal position
     */
    int offsetToAttachmentKey(int offset);

    /**
     * Returns the interpolated attachment slot of the interpolated traversal position based on the interpolated shift
     * of the clothesline network.
     * @param offset the interpolated traversal position
     * @param partialTicks the interpolation parameter
     * @return the interpolated attachment slot
     */
    double offsetToAttachmentKey(double offset, float partialTicks);

    /**
     * Returns the traversal position of the attachment slot based on the current shift of the clothesline network.
     * @param attachmentKey the attachment slot
     * @return a traversal position of the attachment slot
     */
    int attachmentKeyToOffset(int attachmentKey);

    /**
     * Returns the interpolated traversal position of the interpolated attachment slot based on the interpolated shift
     * of the clothesline network.
     * @param attachmentKey the interpolated attachment slot
     * @param partialTicks the interpolation parameter
     * @return the interpolated traversal position
     */
    double attachmentKeyToOffset(double attachmentKey, float partialTicks);
}
