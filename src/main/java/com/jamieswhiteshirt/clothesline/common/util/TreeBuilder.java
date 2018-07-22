package com.jamieswhiteshirt.clothesline.common.util;

import com.jamieswhiteshirt.clothesline.api.*;
import com.jamieswhiteshirt.clothesline.api.util.MutableSortedIntMap;
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
public final class TreeBuilder {
    public static final class SplitResult {
        private final TreeBuilder tree;
        private final List<TreeBuilder> subTrees;

        public SplitResult(TreeBuilder tree, List<TreeBuilder> subTrees) {
            this.tree = tree;
            this.subTrees = subTrees;
        }

        public TreeBuilder getTree() {
            return tree;
        }

        public List<TreeBuilder> getSubTrees() {
            return subTrees;
        }
    }

    private static final class Edge {
        private final EdgeKey key;
        private final MutableSortedIntMap<ItemStack> preAttachments;
        private final TreeBuilder tree;
        private final MutableSortedIntMap<ItemStack> postAttachments;

        private static Edge fromAbsolute(Tree.Edge edge, MutableSortedIntMap<ItemStack> attachments) {
            return new Edge(
                edge.getKey(),
                attachments.shiftedSubMap(edge.getPreMinOffset(), edge.getPreMaxOffset()),
                TreeBuilder.fromAbsolute(edge.getTree(), attachments),
                attachments.shiftedSubMap(edge.getPostMinOffset(), edge.getPostMaxOffset())
            );
        }

        private Edge(EdgeKey key, MutableSortedIntMap<ItemStack> preAttachments, TreeBuilder tree, MutableSortedIntMap<ItemStack> postAttachments) {
            this.key = key;
            this.preAttachments = preAttachments;
            this.tree = tree;
            this.postAttachments = postAttachments;
        }

        private Edge reverse(TreeBuilder parent) {
            return new Edge(key.reverse(parent.pos), postAttachments, parent, preAttachments);
        }

        private Tree.Edge toAbsolute(List<MutableSortedIntMap<ItemStack>> attachmentsList, int fromOffset, TreeBuilder from) {
            attachmentsList.add(preAttachments);
            Tree tree = this.tree.toAbsolute(attachmentsList, fromOffset + key.getLength(), key.reverse(from.pos));
            attachmentsList.add(postAttachments);
            return new Tree.Edge(key, fromOffset, tree);
        }
    }

    public static TreeBuilder fromAbsolute(Tree tree, MutableSortedIntMap<ItemStack> attachments) {
        ArrayList<TreeBuilder.Edge> edges = new ArrayList<>(tree.getEdges().size());
        edges.sort(Comparator.comparing(a -> a.key));
        return new TreeBuilder(tree.getPos(), tree.getEdges().stream().map(
            edge -> Edge.fromAbsolute(edge, attachments)
        ).collect(Collectors.toList()));
    }

    public static TreeBuilder empty(BlockPos pos) {
        return new TreeBuilder(pos, Collections.emptyList());
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

    private TreeBuilder(BlockPos pos, List<Edge> edges) {
        this.pos = pos;
        this.edges = edges;
    }

    public BlockPos getPos() {
        return pos;
    }

    public boolean isEmpty() {
        return edges.isEmpty();
    }

    public boolean addChild(BlockPos pos, TreeBuilder child) {
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

    private void addChild(TreeBuilder child) {
        EdgeKey key = new EdgeKey(pos, child.pos);
        addEdge(new Edge(key, MutableSortedIntMap.empty(key.getLength()), child, MutableSortedIntMap.empty(key.getLength())));
    }

    private void addEdge(Edge edge) {
        int insertionIndex = findEdgeKeyIndex(edge.key);
        edges.add(insertionIndex, edge);
    }

    private void removeEdge(int index) {
        edges.remove(index);
    }

    @Nullable
    private TreeBuilder rerootInner(BlockPos pos) {
        if (this.pos.equals(pos)) {
            return this;
        } else {
            for (int i = 0; i < edges.size(); i++) {
                Edge edge = edges.get(i);
                TreeBuilder childTree = edge.tree;
                TreeBuilder rerooted = childTree.rerootInner(pos);
                if (rerooted != null) {
                    removeEdge(i);
                    childTree.addEdge(edge.reverse(this));
                    return rerooted;
                }
            }
            return null;
        }
    }

    public TreeBuilder reroot(BlockPos pos) {
        TreeBuilder rerooted = rerootInner(pos);
        if (rerooted != null) {
            return rerooted;
        } else {
            throw new IllegalArgumentException("Position is not in TreeBuilder");
        }
    }

    public SplitResult splitNode() {
        List<Edge> edges = this.edges.stream().map(
            edge -> new Edge(edge.key, edge.preAttachments, empty(edge.tree.pos), edge.postAttachments)
        ).collect(Collectors.toList());
        TreeBuilder tree = new TreeBuilder(pos, edges);
        return new SplitResult(tree, this.edges.stream().map(edge -> edge.tree).collect(Collectors.toList()));
    }

    public SplitResult splitEdge(BlockPos edgePos) {
        for (int i = 0; i < this.edges.size(); i++) {
            Edge edge = this.edges.get(i);
            if (edge.key.getPos().equals(edgePos)) {
                TreeBuilder edgeTree = new TreeBuilder(
                    this.pos,
                    new ArrayList<>(Collections.singletonList(new Edge(
                        edge.key,
                        edge.preAttachments,
                        empty(edge.tree.pos),
                        edge.postAttachments
                    )))
                );

                TreeBuilder pastEdgeTree = edge.tree;

                List<Edge> restEdges = new ArrayList<>(this.edges.size() - 1);
                restEdges.addAll(this.edges.subList(0, i));
                restEdges.addAll(this.edges.subList(i + 1, this.edges.size()));
                TreeBuilder restTree = new TreeBuilder(this.pos, restEdges);

                return new SplitResult(edgeTree, Arrays.asList(restTree, pastEdgeTree));
            }
        }
        throw new IllegalArgumentException("Position is not in TreeBuilder");
    }

    public Tree toAbsolute(List<MutableSortedIntMap<ItemStack>> stacksList, int fromOffset) {
        int toOffset = fromOffset;
        ArrayList<Tree.Edge> treeEdges = new ArrayList<>(edges.size());
        for (Edge edge : edges) {
            Tree.Edge staticEdge = edge.toAbsolute(stacksList, toOffset, this);
            treeEdges.add(staticEdge);
            toOffset = staticEdge.getPostMaxOffset();
        }
        return new Tree(pos, treeEdges, fromOffset, toOffset);
    }

    private Tree toAbsolute(List<MutableSortedIntMap<ItemStack>> stacksList, int fromOffset, EdgeKey fromEdgeKey) {
        int toOffset = fromOffset;
        int splitIndex = findEdgeKeyIndex(fromEdgeKey);
        ArrayList<Tree.Edge> treeEdges = new ArrayList<>(edges.size());
        for (Edge edge : edges.subList(splitIndex, edges.size())) {
            Tree.Edge staticEdge = edge.toAbsolute(stacksList, toOffset, this);
            treeEdges.add(staticEdge);
            toOffset = staticEdge.getPostMaxOffset();
        }
        for (Edge edge : edges.subList(0, splitIndex)) {
            Tree.Edge staticEdge = edge.toAbsolute(stacksList, toOffset, this);
            treeEdges.add(staticEdge);
            toOffset = staticEdge.getPostMaxOffset();
        }
        return new Tree(pos, treeEdges, fromOffset, toOffset);
    }
}
