package com.jamieswhiteshirt.clothesline.api;

import com.jamieswhiteshirt.clothesline.api.util.MutableSortedIntMap;
import com.jamieswhiteshirt.clothesline.api.util.RangeLookup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * State container for a network.
 * The structure of the network is immutable, while the attachments and their offsets are mutable.
 * Performant for manipulation of attachments on the network.
 */
public final class AbsoluteNetworkState {
    private static final int MAX_MOMENTUM = 40;

    private int previousOffset;
    private int offset;
    private int momentum;

    private final AbsoluteTree tree;
    private final Map<BlockPos, AbsoluteTree> posLookup;
    private final NodeLoop nodeLoop;
    private final RangeLookup offsetLookup;
    private final MutableSortedIntMap<ItemStack> stacks;

    public static AbsoluteNetworkState createInitial(AbsoluteTree tree) {
        return new AbsoluteNetworkState(
                0,
                0,
                0,
                tree,
                MutableSortedIntMap.empty(tree.getMaxOffset())
        );
    }

    public AbsoluteNetworkState(int previousOffset, int offset, int momentum, AbsoluteTree tree, MutableSortedIntMap<ItemStack> stacks) {
        this.tree = tree;
        this.posLookup = tree.createPositionLookup();
        this.nodeLoop = tree.toNodeLoop();
        this.offsetLookup = RangeLookup.build(0, nodeLoop.getNodes().stream().map(Node::getOffset).collect(Collectors.toList()));
        this.stacks = stacks;
        this.previousOffset = previousOffset;
        this.offset = offset;
        this.momentum = momentum;
    }

    public AbsoluteTree getTree() {
        return tree;
    }

    public AbsoluteTree getSubTree(BlockPos pos) {
        return posLookup.get(pos);
    }

    public NodeLoop getNodeLoop() {
        return nodeLoop;
    }

    public MutableSortedIntMap<ItemStack> getStacks() {
        return stacks;
    }

    public ItemStack getItem(int offset) {
        ItemStack result = stacks.get(offset);
        if (result != null) {
            return result;
        } else {
            return ItemStack.EMPTY;
        }
    }

    public ItemStack insertItem(int offset, ItemStack stack, boolean simulate) {
        if (!stack.isEmpty() && getItem(offset).isEmpty()) {
            if (!simulate) {
                ItemStack insertedItem = stack.copy();
                insertedItem.setCount(1);
                stacks.put(offset, stack);
            }

            ItemStack returnedStack = stack.copy();
            returnedStack.shrink(1);
            return returnedStack;
        }
        return stack;
    }

    public ItemStack extractItem(int offset, boolean simulate) {
        ItemStack result = getItem(offset);
        if (!result.isEmpty() && !simulate) {
            stacks.remove(offset);
        }
        return result;
    }

    public void setItem(int offset, ItemStack stack) {
        if (stack.isEmpty()) {
            stacks.remove(offset);
        } else {
            stacks.put(offset, stack);
        }
    }

    public void removeItem(int offset) {
        stacks.remove(offset);
    }

    public void update() {
        if (momentum > 0) {
            momentum -= 1;
        } else if (momentum < 0) {
            momentum += 1;
        }

        previousOffset = offset;
        offset += momentum;
    }

    public void addMomentum(int momentum) {
        this.momentum = Math.min(Math.max(this.momentum + momentum, -MAX_MOMENTUM), MAX_MOMENTUM);
    }

    public int getOffset() {
        return offset;
    }

    public int getPreviousOffset() {
        return previousOffset;
    }

    public int getMomentum() {
        return momentum;
    }

    public int getMinNodeIndexForOffset(int offset) {
        return offsetLookup.getMinIndex(offset);
    }

    public int getLoopLength() {
        return tree.getLoopLength();
    }
}
