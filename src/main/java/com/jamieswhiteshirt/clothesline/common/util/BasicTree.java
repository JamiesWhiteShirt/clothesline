package com.jamieswhiteshirt.clothesline.common.util;

import com.jamieswhiteshirt.clothesline.api.DeltaKey;
import com.jamieswhiteshirt.clothesline.api.Tree;
import net.minecraft.util.math.BlockPos;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Minimal immutable data structure for a BlockPos tree for serialization purposes.
 * Children are expected to be ordered by {@link DeltaKey#compareTo(DeltaKey)} of their
 * their {@link #getPos()} with this {@link #getPos()} as the origin.
 */
public final class BasicTree {
    public static BasicTree fromAbsolute(Tree tree) {
        return new BasicTree(tree.getPos(), tree.getChildren().stream().map(BasicTree::fromAbsolute).collect(Collectors.toList()), tree.getBaseRotation());
    }

    private final BlockPos pos;
    private final List<BasicTree> children;
    private final int baseRotation;

    public BasicTree(BlockPos pos, List<BasicTree> children, int baseRotation) {
        this.pos = pos;
        this.children = children;
        this.baseRotation = baseRotation;
    }

    public BlockPos getPos() {
        return pos;
    }

    public List<BasicTree> getChildren() {
        return children;
    }

    public int getBaseRotation() {
        return baseRotation;
    }

    private Tree toAbsolute(int fromOffset) {
        int toOffset = fromOffset;
        ArrayList<Tree.Edge> edges = new ArrayList<>(children.size());
        for (BasicTree child : children) {
            DeltaKey key = DeltaKey.between(pos, child.pos);
            Tree staticChild = child.toAbsolute(toOffset + key.getLength());
            Tree.Edge staticEdge = new Tree.Edge(key, toOffset, staticChild);
            edges.add(staticEdge);
            toOffset = staticEdge.getPostMaxOffset();
        }
        return new Tree(pos, edges, fromOffset, toOffset, baseRotation);
    }

    public Tree toAbsolute() {
        return toAbsolute(0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BasicTree basicTree = (BasicTree) o;
        return baseRotation == basicTree.baseRotation &&
            Objects.equals(pos, basicTree.pos) &&
            Objects.equals(children, basicTree.children);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pos, children);
    }

    @Override
    public String toString() {
        return "BasicTree{" +
            "pos=" + pos +
            ", children=" + children +
            ", baseRotation" + baseRotation +
            '}';
    }
}
