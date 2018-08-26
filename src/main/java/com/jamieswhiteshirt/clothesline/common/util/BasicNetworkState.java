package com.jamieswhiteshirt.clothesline.common.util;

import com.jamieswhiteshirt.clothesline.api.INetworkState;
import com.jamieswhiteshirt.clothesline.common.impl.NetworkState;
import com.jamieswhiteshirt.clothesline.api.Tree;
import com.jamieswhiteshirt.clothesline.api.util.MutableSortedIntMap;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class BasicNetworkState {
    public static BasicNetworkState fromAbsolute(INetworkState state) {
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

    public INetworkState toAbsolute() {
        Tree tree = this.tree.toAbsolute();
        MutableSortedIntMap<ItemStack> attachments = new MutableSortedIntMap<>(
                new ArrayList<>(this.attachments.stream().map(
                        attachment -> new MutableSortedIntMap.Entry<>(attachment.getKey(), attachment.getStack())
                ).collect(Collectors.toList())),
                tree.getTraversalLength()
        );
        return new NetworkState(
                shift,
                shift,
                momentum,
                momentum,
                tree,
                attachments
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BasicNetworkState that = (BasicNetworkState) o;
        return shift == that.shift &&
            momentum == that.momentum &&
            Objects.equals(tree, that.tree) &&
            Objects.equals(attachments, that.attachments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shift, momentum, tree, attachments);
    }

    @Override
    public String toString() {
        return "BasicNetworkState{" +
            "shift=" + shift +
            ", momentum=" + momentum +
            ", tree=" + tree +
            ", attachments=" + attachments +
            '}';
    }
}
