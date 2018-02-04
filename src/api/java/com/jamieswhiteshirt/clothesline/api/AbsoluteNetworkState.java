package com.jamieswhiteshirt.clothesline.api;

import com.jamieswhiteshirt.clothesline.api.util.MathUtil;
import com.jamieswhiteshirt.clothesline.api.util.MutableSortedIntMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.Map;

/**
 * State container for a network.
 * The structure of the network is immutable, while the attachments and their keys are mutable.
 * Performant for manipulation of attachments on the network.
 */
public final class AbsoluteNetworkState {
    public static final int MAX_MOMENTUM = 40;

    private int previousShift;
    private int shift;
    private int previousMomentum;
    private int momentum;

    private final AbsoluteTree tree;
    private final Graph graph;
    private final Map<BlockPos, AbsoluteTree> posLookup;
    private final MutableSortedIntMap<ItemStack> attachments;

    public static AbsoluteNetworkState createInitial(AbsoluteTree tree) {
        return new AbsoluteNetworkState(
                0,
                0,
                0,
                0,
                tree,
                MutableSortedIntMap.empty(tree.getMaxOffset())
        );
    }

    public AbsoluteNetworkState(int previousShift, int shift, int previousMomentum, int momentum, AbsoluteTree tree, MutableSortedIntMap<ItemStack> attachments) {
        this.tree = tree;
        this.graph = tree.buildGraph();
        this.posLookup = tree.createPositionLookup();
        this.attachments = attachments;
        this.previousShift = previousShift;
        this.previousMomentum = previousMomentum;
        this.shift = shift;
        this.momentum = momentum;
    }

    private int lengthMod(int i) {
        return Math.floorMod(i, getLoopLength());
    }

    public AbsoluteTree getTree() {
        return tree;
    }

    public Graph getGraph() {
        return graph;
    }

    public AbsoluteTree getSubTree(BlockPos pos) {
        return posLookup.get(pos);
    }

    public MutableSortedIntMap<ItemStack> getAttachments() {
        return attachments;
    }

    public List<MutableSortedIntMap.Entry<ItemStack>> getAttachmentsInRange(int minAttachmentKey, int maxAttachmentKey) {
        return attachments.getInRange(lengthMod(minAttachmentKey), lengthMod(maxAttachmentKey));
    }

    public ItemStack getAttachment(int attachmentKey) {
        ItemStack result = attachments.get(lengthMod(attachmentKey));
        if (result != null) {
            return result;
        } else {
            return ItemStack.EMPTY;
        }
    }

    public void setAttachment(int attachmentKey, ItemStack stack) {
        if (stack.isEmpty()) {
            attachments.remove(lengthMod(attachmentKey));
        } else {
            attachments.put(lengthMod(attachmentKey), stack);
        }
    }

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

    public void addMomentum(int momentum) {
        this.momentum = Math.min(Math.max(this.momentum + momentum, -MAX_MOMENTUM), MAX_MOMENTUM);
    }

    public int getShift() {
        return shift;
    }

    public int getPreviousShift() {
        return previousShift;
    }

    public double getShift(float partialTicks) {
        return previousShift + (shift - previousShift) * partialTicks;
    }

    public int getMomentum() {
        return momentum;
    }

    public int getPreviousMomentum() {
        return previousMomentum;
    }

    public double getMomentum(float partialTicks) {
        return previousMomentum + (momentum - previousMomentum) * partialTicks;
    }

    public int getLoopLength() {
        return tree.getLoopLength();
    }

    public int offsetToAttachmentKey(int offset) {
        return Math.floorMod(offset - shift, getLoopLength());
    }

    public double offsetToAttachmentKey(double offset, float partialTicks) {
        return MathUtil.floorMod(offset - getShift(partialTicks), getLoopLength());
    }

    public int attachmentKeyToOffset(int attachmentKey) {
        return Math.floorMod(attachmentKey + shift, getLoopLength());
    }

    public double attachmentKeyToOffset(double attachmentKey, float partialTicks) {
        return MathUtil.floorMod(attachmentKey + getShift(partialTicks), getLoopLength());
    }
}
