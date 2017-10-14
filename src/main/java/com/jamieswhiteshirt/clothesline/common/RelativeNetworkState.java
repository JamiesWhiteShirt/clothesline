package com.jamieswhiteshirt.clothesline.common;

import com.jamieswhiteshirt.clothesline.api.AbsoluteTree;
import com.jamieswhiteshirt.clothesline.api.AbsoluteNetworkState;
import com.jamieswhiteshirt.clothesline.api.util.SortedIntShiftMap;
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
        SortedIntShiftMap<ItemStack> attachments = state.getStacks();
        int midOffset = Math.floorMod(-state.getOffset(), attachments.getMaxKey());
        SortedIntShiftMap<ItemStack> shiftedItemStacks = SortedIntShiftMap.concatenate(Arrays.asList(
                attachments.subMap(midOffset, attachments.getMaxKey()),
                attachments.subMap(0, midOffset)
        ));
        RelativeTree tree = RelativeTree.fromAbsolute(state.getTree(), shiftedItemStacks);
        return new RelativeNetworkState(state.getMomentum(), tree);
    }

    private final int momentum;
    private final RelativeTree tree;

    private RelativeNetworkState(int momentum, RelativeTree tree) {
        this.momentum = momentum;
        this.tree = tree;
    }

    public RelativeNetworkState reroot(BlockPos pos) {
        return new RelativeNetworkState(momentum, tree.reroot(pos));
    }

    public RelativeNetworkState addEdge(BlockPos fromPos, BlockPos toPos) {
        RelativeTree newTree = tree.reroot(fromPos).addChild(RelativeTree.empty(toPos));
        return new RelativeNetworkState(momentum, newTree);
    }

    public RelativeNetworkState mergeWith(BlockPos thisPos, BlockPos otherPos, RelativeNetworkState other) {
        RelativeTree thisTree = tree.reroot(thisPos);
        RelativeTree otherTree = other.tree.reroot(otherPos);
        RelativeTree mergedTree = thisTree.addChild(otherTree);
        return new RelativeNetworkState((momentum + other.momentum) / 2, mergedTree);
    }

    public SplitResult split(BlockPos splitPos) {
        RelativeTree.SplitResult result = tree.reroot(splitPos).split();
        return new SplitResult(
                new RelativeNetworkState(momentum, result.getTree()),
                result.getSubTrees().stream().filter(tree -> !tree.isEmpty()).map(
                        tree -> new RelativeNetworkState(momentum, tree)
                ).collect(Collectors.toList())
        );
    }

    public AbsoluteNetworkState toAbsolute() {
        LinkedList<SortedIntShiftMap<ItemStack>> attachmentsList = new LinkedList<>();
        AbsoluteTree absoluteTree = tree.toAbsolute(attachmentsList, 0);
        return new AbsoluteNetworkState(0, 0, momentum, absoluteTree, SortedIntShiftMap.concatenate(attachmentsList));
    }
}
