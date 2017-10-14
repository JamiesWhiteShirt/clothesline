package com.jamieswhiteshirt.clothesline.common;

import com.jamieswhiteshirt.clothesline.api.*;
import com.jamieswhiteshirt.clothesline.api.util.SortedIntShiftMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Intermediate and pliable data structure of a network as a tree.
 * All edges and subtrees are relative to each other, which means this structure is suitable for operations that
 * modify the structure of the network.
 */
public class RelativeTree {
    public static class SplitResult {
        private final RelativeTree tree;
        private final List<RelativeTree> subTrees;

        public SplitResult(RelativeTree tree, List<RelativeTree> subTrees) {
            this.tree = tree;
            this.subTrees = subTrees;
        }

        public RelativeTree getTree() {
            return tree;
        }

        public List<RelativeTree> getSubTrees() {
            return subTrees;
        }
    }

    private static class Edge {
        private final EdgeKey key;
        private final SortedIntShiftMap<ItemStack> preStacks;
        private final RelativeTree tree;
        private final SortedIntShiftMap<ItemStack> postStacks;

        private static Edge fromAbsolute(AbsoluteTree.Edge edge, SortedIntShiftMap<ItemStack> attachments) {
            return new Edge(
                    edge.getKey(),
                    attachments.subMap(edge.getPreMinOffset(), edge.getPreMaxOffset()),
                    RelativeTree.fromAbsolute(edge.getTree(), attachments),
                    attachments.subMap(edge.getPostMinOffset(), edge.getPostMaxOffset())
            );
        }

        private Edge(EdgeKey key, SortedIntShiftMap<ItemStack> preStacks, RelativeTree tree, SortedIntShiftMap<ItemStack> postStacks) {
            this.key = key;
            this.preStacks = preStacks;
            this.tree = tree;
            this.postStacks = postStacks;
        }

        private int getLoopLength() {
            return key.getLength() * 2 + tree.getLoopLength();
        }

        private Edge reverse(RelativeTree parent) {
            return new Edge(key.reverse(parent.pos), postStacks, parent, preStacks);
        }

        private AbsoluteTree.Edge toAbsolute(List<SortedIntShiftMap<ItemStack>> attachmentsList, int fromOffset, RelativeTree from) {
            attachmentsList.add(preStacks);
            AbsoluteTree absoluteTree = tree.toAbsolute(attachmentsList, fromOffset + key.getLength(), key.reverse(from.pos));
            attachmentsList.add(postStacks);
            return new AbsoluteTree.Edge(key, fromOffset, absoluteTree);
        }
    }

    public static RelativeTree fromAbsolute(AbsoluteTree absoluteTree, SortedIntShiftMap<ItemStack> attachments) {
        ArrayList<RelativeTree.Edge> edges = new ArrayList<>(absoluteTree.getEdges().size());
        edges.sort(Comparator.comparing(a -> a.key));
        return new RelativeTree(absoluteTree.getPos(), absoluteTree.getEdges().stream().map(
                edge -> Edge.fromAbsolute(edge, attachments)
        ).collect(Collectors.toList()), absoluteTree.getLoopLength());
    }

    public static RelativeTree empty(BlockPos pos) {
        return new RelativeTree(pos, Collections.emptyList(), 0);
    }

    private int findEdgeKeyIndex(EdgeKey edgeKey, int minIndex, int maxIndex) {
        if (minIndex != maxIndex) {
            int middleIndex = (minIndex + maxIndex) / 2;
            int comparison = edgeKey.compareTo(edges.get(middleIndex).key);
            if (comparison < 0) {
                return findEdgeKeyIndex(edgeKey, minIndex, middleIndex);
            } else if (comparison > 0) {
                return findEdgeKeyIndex(edgeKey, middleIndex + 1, maxIndex);
            } else {
                return middleIndex;
            }
        } else {
            return minIndex;
        }
    }

    public int findEdgeKeyIndex(EdgeKey edgeKey) {
        return findEdgeKeyIndex(edgeKey, 0, edges.size());
    }

    private final BlockPos pos;
    private final List<Edge> edges;
    private final int loopLength;

    private RelativeTree(BlockPos pos, List<Edge> edges, int loopLength) {
        this.pos = pos;
        this.edges = edges;
        this.loopLength = loopLength;
    }

    public int getLoopLength() {
        return loopLength;
    }

    public BlockPos getPos() {
        return pos;
    }

    public boolean isEmpty() {
        return edges.isEmpty();
    }

    public boolean addChild(BlockPos pos, RelativeTree child) {
        if (this.pos.equals(pos)) {
            addChild(child);
            return true;
        } else {
            for (Edge edge : edges) {
                if (edge.tree.addChild(pos, child)) {
                    return true;
                }
            }
            return false;
        }
    }

    private void addChild(RelativeTree child) {
        EdgeKey key = new EdgeKey(pos, child.pos);
        addEdge(new Edge(key, SortedIntShiftMap.empty(key.getLength()), child, SortedIntShiftMap.empty(key.getLength())));
    }

    private void addEdge(Edge edge) {
        int insertionIndex = findEdgeKeyIndex(edge.key);
        edges.add(insertionIndex, edge);
    }

    private void removeEdge(int index) {
        edges.remove(index);
    }

    @Nullable
    private RelativeTree rerootInner(BlockPos pos) {
        if (this.pos.equals(pos)) {
            return this;
        } else {
            for (int i = 0; i < edges.size(); i++) {
                Edge edge = edges.get(i);
                RelativeTree childTree = edge.tree;
                RelativeTree rerooted = childTree.rerootInner(pos);
                if (rerooted != null) {
                    removeEdge(i);
                    childTree.addEdge(edge.reverse(this));
                    return rerooted;
                }
            }
            return null;
        }
    }

    public RelativeTree reroot(BlockPos pos) {
        RelativeTree rerooted = rerootInner(pos);
        if (rerooted != null) {
            return rerooted;
        } else {
            throw new IllegalArgumentException("Position is not in RelativeTree");
        }
    }

    public SplitResult split() {
        List<Edge> edges = this.edges.stream().map(
                edge -> new Edge(edge.key, edge.preStacks, empty(edge.tree.pos), edge.postStacks)
        ).collect(Collectors.toList());
        RelativeTree tree = new RelativeTree(pos, edges, edges.stream().mapToInt(Edge::getLoopLength).sum());
        return new SplitResult(tree, this.edges.stream().map(edge -> edge.tree).collect(Collectors.toList()));
    }

    public AbsoluteTree toAbsolute(List<SortedIntShiftMap<ItemStack>> stacksList, int fromOffset) {
        int toOffset = fromOffset;
        ArrayList<AbsoluteTree.Edge> treeEdges = new ArrayList<>(edges.size());
        for (Edge edge : edges) {
            AbsoluteTree.Edge staticEdge = edge.toAbsolute(stacksList, toOffset, this);
            treeEdges.add(staticEdge);
            toOffset = staticEdge.getPostMaxOffset();
        }
        return new AbsoluteTree(pos, treeEdges, fromOffset, toOffset);
    }

    private AbsoluteTree toAbsolute(List<SortedIntShiftMap<ItemStack>> stacksList, int fromOffset, EdgeKey fromEdgeKey) {
        int toOffset = fromOffset;
        int splitIndex = findEdgeKeyIndex(fromEdgeKey);
        ArrayList<AbsoluteTree.Edge> treeEdges = new ArrayList<>(edges.size());
        for (Edge edge : edges.subList(splitIndex, edges.size())) {
            AbsoluteTree.Edge staticEdge = edge.toAbsolute(stacksList, toOffset, this);
            treeEdges.add(staticEdge);
            toOffset = staticEdge.getPostMaxOffset();
        }
        for (Edge edge : edges.subList(0, splitIndex)) {
            AbsoluteTree.Edge staticEdge = edge.toAbsolute(stacksList, toOffset, this);
            treeEdges.add(staticEdge);
            toOffset = staticEdge.getPostMaxOffset();
        }
        return new AbsoluteTree(pos, treeEdges, fromOffset, toOffset);
    }
}
