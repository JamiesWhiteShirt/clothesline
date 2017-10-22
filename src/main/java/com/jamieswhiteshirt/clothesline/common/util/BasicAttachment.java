package com.jamieswhiteshirt.clothesline.common.util;

import net.minecraft.item.ItemStack;

public class BasicAttachment {
    private final int offset;
    private final ItemStack stack;

    public BasicAttachment(int offset, ItemStack stack) {
        this.offset = offset;
        this.stack = stack;
    }

    public int getOffset() {
        return offset;
    }

    public ItemStack getStack() {
        return stack;
    }
}
