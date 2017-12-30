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
                state.getShift(),
                state.getMomentum(),
                BasicTree.fromAbsolute(state.getTree()),
                state.getAttachments().entries().stream().map(
                        entry -> new BasicAttachment(entry.getKey(), entry.getValue().copy())
                ).collect(Collectors.toList())
        );
    }

    private final int shift;
    private final int momentum;
    private final BasicTree tree;
    private final List<BasicAttachment> attachments;

    public BasicNetworkState(int shift, int momentum, BasicTree tree, List<BasicAttachment> attachments) {
        this.shift = shift;
        this.momentum = momentum;
        this.tree = tree;
        this.attachments = attachments;
    }

    public int getShift() {
        return shift;
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
        MutableSortedIntMap<ItemStack> attachments = new MutableSortedIntMap<>(
                new ArrayList<>(this.attachments.stream().map(
                        attachment -> new MutableSortedIntMap.Entry<>(attachment.getKey(), attachment.getStack())
                ).collect(Collectors.toList())),
                tree.getLoopLength()
        );
        return new AbsoluteNetworkState(
                shift,
                shift,
                momentum,
                momentum,
                tree,
                attachments
        );
    }
}
