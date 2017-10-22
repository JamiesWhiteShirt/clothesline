package com.jamieswhiteshirt.clothesline.common.util;

import com.jamieswhiteshirt.clothesline.api.AbsoluteNetworkState;
import com.jamieswhiteshirt.clothesline.api.AbsoluteTree;
import com.jamieswhiteshirt.clothesline.api.util.MutableSortedIntMap;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BasicNetworkState {
    public static BasicNetworkState fromAbsolute(AbsoluteNetworkState state) {
        return new BasicNetworkState(
                state.getOffset(),
                state.getMomentum(),
                BasicTree.fromAbsolute(state.getTree()),
                state.getStacks().entries().stream().map(
                        entry -> new BasicAttachment(entry.getKey(), entry.getValue().copy())
                ).collect(Collectors.toList())
        );
    }

    private final int offset;
    private final int momentum;
    private final BasicTree tree;
    private final List<BasicAttachment> attachments;

    public BasicNetworkState(int offset, int momentum, BasicTree tree, List<BasicAttachment> attachments) {
        this.offset = offset;
        this.momentum = momentum;
        this.tree = tree;
        this.attachments = attachments;
    }

    public int getOffset() {
        return offset;
    }

    public int getMomentum() {
        return momentum;
    }

    public BasicTree getTree() {
        return tree;
    }

    public List<BasicAttachment> getAttachments() {
        return attachments;
    }

    public AbsoluteNetworkState toAbsolute() {
        AbsoluteTree tree = this.tree.toAbsolute();
        MutableSortedIntMap<ItemStack> stacks = new MutableSortedIntMap<>(
                new ArrayList<>(attachments.stream().map(
                        attachment -> new MutableSortedIntMap.Entry<>(attachment.getOffset(), attachment.getStack())
                ).collect(Collectors.toList())),
                tree.getLoopLength()
        );
        return new AbsoluteNetworkState(
                offset,
                offset,
                momentum,
                tree,
                stacks
        );
    }
}
