package com.jamieswhiteshirt.clothesline.common.util;

import net.minecraft.item.ItemStack;

public class BasicAttachment {
    private final int key;
    private final ItemStack stack;

    public BasicAttachment(int key, ItemStack stack) {
        this.key = key;
        this.stack = stack;
    }

    public int getKey() {
        return key;
    }

    public ItemStack getStack() {
        return stack;
    }
}
