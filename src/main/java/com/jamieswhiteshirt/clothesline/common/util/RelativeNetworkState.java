package com.jamieswhiteshirt.clothesline.common.util;

import com.jamieswhiteshirt.clothesline.api.AbsoluteTree;
import com.jamieswhiteshirt.clothesline.api.AbsoluteNetworkState;
import com.jamieswhiteshirt.clothesline.api.util.MutableSortedIntMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class RelativeNetworkState {
    public static class SplitResult {
        private final RelativeNetworkState state;
        private final List<RelativeNetworkState> subStates;

        public SplitResult(RelativeNetworkState state, List<RelativeNetworkState> subStates) {
            this.state = state;
            this.subStates = subStates;
        }

        public RelativeNetworkState getState() {
            return state;
        }

        public List<RelativeNetworkState> getSubStates() {
            return subStates;
        }
    }

    public static RelativeNetworkState fromAbsolute(AbsoluteNetworkState state) {
        MutableSortedIntMap<ItemStack> attachments = state.getAttachments();
        int midOffset = Math.floorMod(-state.getOffset(), attachments.getMaxKey());
        MutableSortedIntMap<ItemStack> shiftedItemStacks = MutableSortedIntMap.concatenate(Arrays.asList(
                attachments.shiftedSubMap(midOffset, attachments.getMaxKey()),
                attachments.shiftedSubMap(0, midOffset)
        ));
        RelativeTree tree = RelativeTree.fromAbsolute(state.getTree(), shiftedItemStacks);
        return new RelativeNetworkState(state.getMomentum(), tree);
    }

    private int momentum;
    private RelativeTree treeRoot;

    private RelativeNetworkState(int momentum, RelativeTree treeRoot) {
        this.momentum = momentum;
        this.treeRoot = treeRoot;
    }

    public void reroot(BlockPos pos) {
        treeRoot = treeRoot.reroot(pos);
    }

    public void addEdge(BlockPos fromPos, BlockPos toPos) {
        treeRoot.addChild(fromPos, RelativeTree.empty(toPos));
        momentum /= 2;
    }

    public void addSubState(BlockPos fromPos, RelativeNetworkState other) {
        treeRoot.addChild(fromPos, other.treeRoot);
        momentum = (momentum + other.momentum) / 2;
    }

    public SplitResult splitRoot() {
        RelativeTree.SplitResult result = treeRoot.split();
        return new SplitResult(
                new RelativeNetworkState(momentum, result.getTree()),
                result.getSubTrees().stream().filter(tree -> !tree.isEmpty()).map(
                        tree -> new RelativeNetworkState(momentum, tree)
                ).collect(Collectors.toList())
        );
    }

    public AbsoluteNetworkState toAbsolute() {
        LinkedList<MutableSortedIntMap<ItemStack>> attachmentsList = new LinkedList<>();
        AbsoluteTree absoluteTree = treeRoot.toAbsolute(attachmentsList, 0);
        return new AbsoluteNetworkState(0, 0, momentum, absoluteTree, MutableSortedIntMap.concatenate(attachmentsList));
    }
}
