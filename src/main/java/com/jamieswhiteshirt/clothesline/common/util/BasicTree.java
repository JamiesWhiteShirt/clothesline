package com.jamieswhiteshirt.clothesline.common.util;

import com.jamieswhiteshirt.clothesline.api.EdgeKey;
import com.jamieswhiteshirt.clothesline.api.Measurements;
import com.jamieswhiteshirt.clothesline.api.AbsoluteTree;
import net.minecraft.util.math.BlockPos;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Minimal immutable data structure for a BlockPos tree for serialization purposes.
 * Children are expected to be ordered by {@link EdgeKey#compareTo(EdgeKey)} of their
 * their {@link #getPos()} with this {@link #getPos()} as the origin.
 */
public class BasicTree {
    public static BasicTree fromAbsolute(AbsoluteTree absoluteTree) {
        return new BasicTree(absoluteTree.getPos(), absoluteTree.getChildren().stream().map(BasicTree::fromAbsolute).collect(Collectors.toList()));
    }

    public static BasicTree createInitial(BlockPos fromPos, BlockPos toPos) {
        return new BasicTree(fromPos, Collections.singletonList(new BasicTree(toPos, Collections.emptyList())));
    }

    private final BlockPos pos;
    private final List<BasicTree> children;

    public BasicTree(BlockPos pos, List<BasicTree> children) {
        this.pos = pos;
        this.children = children;
    }

    public BlockPos getPos() {
        return pos;
    }

    public List<BasicTree> getChildren() {
        return children;
    }

    private AbsoluteTree toAbsolute(int fromOffset) {
        int toOffset = fromOffset;
        ArrayList<AbsoluteTree.Edge> edges = new ArrayList<>(children.size());
        for (BasicTree child : children) {
            EdgeKey key = new EdgeKey(pos, child.pos);
            AbsoluteTree staticChild = child.toAbsolute(toOffset + key.getLength());
            AbsoluteTree.Edge staticEdge = new AbsoluteTree.Edge(key, toOffset, staticChild);
            edges.add(staticEdge);
            toOffset = staticEdge.getPostMaxOffset();
        }
        return new AbsoluteTree(pos, edges, fromOffset, toOffset);
    }

    public AbsoluteTree toAbsolute() {
        return toAbsolute(0);
    }
}
