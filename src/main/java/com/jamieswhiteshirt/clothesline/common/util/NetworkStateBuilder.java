package com.jamieswhiteshirt.clothesline.common.util;

import com.jamieswhiteshirt.clothesline.api.INetworkState;
import com.jamieswhiteshirt.clothesline.api.Path;
import com.jamieswhiteshirt.clothesline.api.Tree;
import com.jamieswhiteshirt.clothesline.common.impl.NetworkState;
import com.jamieswhiteshirt.clothesline.api.util.MutableSortedIntMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
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

    public static NetworkStateBuilder fromAbsolute(INetworkState state) {
        MutableSortedIntMap<ItemStack> attachments = state.getAttachments();
        MutableSortedIntMap<ItemStack> itemStacks;
        if (state.getPathLength() != 0) {
            int midAttachmentKey = state.offsetToAttachmentKey(0);
            itemStacks = MutableSortedIntMap.concatenate(Arrays.asList(
                attachments.shiftedSubMap(midAttachmentKey, attachments.getMaxKey()),
                attachments.shiftedSubMap(0, midAttachmentKey)
            ));
        } else {
            itemStacks = state.getAttachments();
        }
        TreeBuilder tree = TreeBuilder.fromAbsolute(state.getTree(), itemStacks, state.getShift());
        return new NetworkStateBuilder(state.getMomentum(), tree);
    }

    public static NetworkStateBuilder emptyRoot(int momentum, BlockPos root) {
        return new NetworkStateBuilder(momentum, TreeBuilder.emptyRoot(root));
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
        treeRoot.addChild(fromPos, TreeBuilder.emptyRoot(toPos));
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
            result.getSubTrees().stream()
                .filter(tree -> !tree.isEmpty())
                .map(tree -> new NetworkStateBuilder(momentum, tree))
                .collect(Collectors.toList())
        );
    }

    public SplitResult splitEdge(BlockPos pos) {
        TreeBuilder.SplitResult result = treeRoot.splitEdge(pos);
        return new SplitResult(
            new NetworkStateBuilder(momentum, result.getTree()),
            result.getSubTrees().stream()
                .filter(tree -> !tree.isEmpty())
                .map(tree -> new NetworkStateBuilder(momentum, tree))
                .collect(Collectors.toList())
        );
    }

    public INetworkState build() {
        LinkedList<MutableSortedIntMap<ItemStack>> attachmentsList = new LinkedList<>();
        Tree tree = treeRoot.build(attachmentsList, 0);
        Path path = PathBuilder.buildPath(tree);
        return new NetworkState(0, 0, momentum, momentum, tree, path, MutableSortedIntMap.concatenate(attachmentsList));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NetworkStateBuilder that = (NetworkStateBuilder) o;
        return momentum == that.momentum &&
            Objects.equals(treeRoot, that.treeRoot);
    }

    @Override
    public int hashCode() {
        return Objects.hash(momentum, treeRoot);
    }
}
