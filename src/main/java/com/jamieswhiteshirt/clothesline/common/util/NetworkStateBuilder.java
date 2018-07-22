package com.jamieswhiteshirt.clothesline.common.util;

import com.jamieswhiteshirt.clothesline.api.Tree;
import com.jamieswhiteshirt.clothesline.api.NetworkState;
import com.jamieswhiteshirt.clothesline.api.util.MutableSortedIntMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public final class NetworkStateBuilder {
    public static final class SplitResult {
        private final NetworkStateBuilder state;
        private final List<NetworkStateBuilder> subStates;

        public SplitResult(NetworkStateBuilder state, List<NetworkStateBuilder> subStates) {
            this.state = state;
            this.subStates = subStates;
        }

        public NetworkStateBuilder getState() {
            return state;
        }

        public List<NetworkStateBuilder> getSubStates() {
            return subStates;
        }
    }

    public static NetworkStateBuilder fromAbsolute(NetworkState state) {
        MutableSortedIntMap<ItemStack> attachments = state.getAttachments();
        int midAttachmentKey = state.offsetToAttachmentKey(0);
        MutableSortedIntMap<ItemStack> shiftedItemStacks = MutableSortedIntMap.concatenate(Arrays.asList(
            attachments.shiftedSubMap(midAttachmentKey, attachments.getMaxKey()),
            attachments.shiftedSubMap(0, midAttachmentKey)
        ));
        TreeBuilder tree = TreeBuilder.fromAbsolute(state.getTree(), shiftedItemStacks);
        return new NetworkStateBuilder(state.getMomentum(), tree);
    }

    private int momentum;
    private TreeBuilder treeRoot;

    private NetworkStateBuilder(int momentum, TreeBuilder treeRoot) {
        this.momentum = momentum;
        this.treeRoot = treeRoot;
    }

    public void reroot(BlockPos pos) {
        treeRoot = treeRoot.reroot(pos);
    }

    public void addEdge(BlockPos fromPos, BlockPos toPos) {
        treeRoot.addChild(fromPos, TreeBuilder.empty(toPos));
        momentum /= 2;
    }

    public void addSubState(BlockPos fromPos, NetworkStateBuilder other) {
        treeRoot.addChild(fromPos, other.treeRoot);
        momentum = (momentum + other.momentum) / 2;
    }

    public SplitResult splitRoot() {
        TreeBuilder.SplitResult result = treeRoot.splitNode();
        return new SplitResult(
            new NetworkStateBuilder(momentum, result.getTree()),
            result.getSubTrees().stream().filter(tree -> !tree.isEmpty()).map(
                tree -> new NetworkStateBuilder(momentum, tree)
            ).collect(Collectors.toList())
        );
    }

    public SplitResult splitEdge(BlockPos pos) {
        TreeBuilder.SplitResult result = treeRoot.splitEdge(pos);
        return new SplitResult(
            new NetworkStateBuilder(momentum, result.getTree()),
            result.getSubTrees().stream().filter(tree -> !tree.isEmpty()).map(
                tree -> new NetworkStateBuilder(momentum, tree)
            ).collect(Collectors.toList())
        );
    }

    public NetworkState toAbsolute() {
        LinkedList<MutableSortedIntMap<ItemStack>> attachmentsList = new LinkedList<>();
        Tree tree = treeRoot.toAbsolute(attachmentsList, 0);
        return new NetworkState(0, 0, momentum, momentum, tree, MutableSortedIntMap.concatenate(attachmentsList));
    }
}
