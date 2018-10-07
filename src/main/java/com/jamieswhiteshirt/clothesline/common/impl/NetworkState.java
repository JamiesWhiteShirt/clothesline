package com.jamieswhiteshirt.clothesline.common.impl;

import com.jamieswhiteshirt.clothesline.api.Path;
import com.jamieswhiteshirt.clothesline.api.INetworkState;
import com.jamieswhiteshirt.clothesline.api.Tree;
import com.jamieswhiteshirt.clothesline.api.util.MathUtil;
import com.jamieswhiteshirt.clothesline.api.util.MutableSortedIntMap;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * State container for a network.
 * The structure of the network is immutable, while the attachments and their keys are mutable.
 * Performant for manipulation of attachments on the network.
 */
public final class NetworkState implements INetworkState {

    private int previousShift;
    private int shift;
    private int previousMomentum;
    private int momentum;

    private final Tree tree;
    private final Path path;
    private final LongSet chunkSpan;
    private final MutableSortedIntMap<ItemStack> attachments;

    public NetworkState(int previousShift, int shift, int previousMomentum, int momentum, Tree tree, Path path, LongSet chunkSpan, MutableSortedIntMap<ItemStack> attachments) {
        this.tree = tree;
        this.path = path;
        this.chunkSpan = chunkSpan;
        this.attachments = attachments;
        this.previousShift = previousShift;
        this.previousMomentum = previousMomentum;
        this.shift = shift;
        this.momentum = momentum;
    }

    private int lengthMod(int i) {
        return Math.floorMod(i, getPathLength());
    }

    @Override
    public Tree getTree() {
        return tree;
    }

    @Override
    public Path getPath() {
        return path;
    }

    @Override
    public LongSet getChunkSpan() {
        return chunkSpan;
    }

    @Override
    public MutableSortedIntMap<ItemStack> getAttachments() {
        return attachments;
    }

    @Override
    public List<MutableSortedIntMap.Entry<ItemStack>> getAttachmentsInRange(int minAttachmentKey, int maxAttachmentKey) {
        return attachments.getInRange(lengthMod(minAttachmentKey), lengthMod(maxAttachmentKey));
    }

    @Override
    public ItemStack getAttachment(int attachmentKey) {
        ItemStack result = attachments.get(lengthMod(attachmentKey));
        if (result != null) {
            return result;
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public void setAttachment(int attachmentKey, ItemStack stack) {
        if (stack.isEmpty()) {
            attachments.remove(lengthMod(attachmentKey));
        } else {
            attachments.put(lengthMod(attachmentKey), stack);
        }
    }

    @Override
    public void update() {
        previousMomentum = momentum;
        previousShift = shift;

        if (momentum > 0) {
            momentum -= 1;
        } else if (momentum < 0) {
            momentum += 1;
        }

        shift += momentum;
    }

    @Override
    public int getShift() {
        return shift;
    }

    @Override
    public void setShift(int shift) {
        this.shift = shift;
    }

    @Override
    public int getPreviousShift() {
        return previousShift;
    }

    @Override
    public double getShift(float partialTicks) {
        return previousShift + (shift - previousShift) * partialTicks;
    }

    @Override
    public int getMomentum() {
        return momentum;
    }

    @Override
    public void setMomentum(int momentum) {
        this.momentum = Math.min(Math.max(momentum, -MAX_MOMENTUM), MAX_MOMENTUM);
    }

    @Override
    public int getPreviousMomentum() {
        return previousMomentum;
    }

    @Override
    public double getMomentum(float partialTicks) {
        return previousMomentum + (momentum - previousMomentum) * partialTicks;
    }

    @Override
    public int getPathLength() {
        return path.getLength();
    }

    @Override
    public int offsetToAttachmentKey(int offset) {
        return Math.floorMod(offset - shift, getPathLength());
    }

    @Override
    public double offsetToAttachmentKey(double offset, float partialTicks) {
        return MathUtil.floorMod(offset - getShift(partialTicks), getPathLength());
    }

    @Override
    public int attachmentKeyToOffset(int attachmentKey) {
        return Math.floorMod(attachmentKey + shift, getPathLength());
    }

    @Override
    public double attachmentKeyToOffset(double attachmentKey, float partialTicks) {
        return MathUtil.floorMod(attachmentKey + getShift(partialTicks), getPathLength());
    }
}
