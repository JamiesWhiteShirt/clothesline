package com.jamieswhiteshirt.clothesline.api;

import net.minecraft.item.ItemStack;

public class Attachment {
    private final int id;
    private final int offset;
    private final ItemStack stack;

    public Attachment(int id, int offset, ItemStack stack) {
        this.id = id;
        this.offset = offset;
        this.stack = stack;
    }

    public int getId() {
        return id;
    }

    public int getOffset() {
        return offset;
    }

    public ItemStack getStack() {
        return stack;
    }
}
