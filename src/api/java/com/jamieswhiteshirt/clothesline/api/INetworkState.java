package com.jamieswhiteshirt.clothesline.api;

import com.jamieswhiteshirt.clothesline.api.util.MutableSortedIntMap;
import net.minecraft.item.ItemStack;

import java.util.List;

public interface INetworkState {
    int MAX_MOMENTUM = 40;

    Tree getTree();

    Graph getGraph();

    MutableSortedIntMap<ItemStack> getAttachments();

    List<MutableSortedIntMap.Entry<ItemStack>> getAttachmentsInRange(int minAttachmentKey, int maxAttachmentKey);

    ItemStack getAttachment(int attachmentKey);

    void setAttachment(int attachmentKey, ItemStack stack);

    void update();

    int getShift();

    void setShift(int shift);

    int getPreviousShift();

    double getShift(float partialTicks);

    int getMomentum();

    void setMomentum(int momentum);

    int getPreviousMomentum();

    double getMomentum(float partialTicks);

    int getLoopLength();

    int offsetToAttachmentKey(int offset);

    double offsetToAttachmentKey(double offset, float partialTicks);

    int attachmentKeyToOffset(int attachmentKey);

    double attachmentKeyToOffset(double attachmentKey, float partialTicks);
}
