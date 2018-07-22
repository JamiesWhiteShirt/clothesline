package com.jamieswhiteshirt.clothesline.common.util;

import net.minecraft.item.ItemStack;

import java.util.Objects;

public final class BasicAttachment {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BasicAttachment that = (BasicAttachment) o;
        return key == that.key &&
            ItemStack.areItemStacksEqual(stack, that.stack);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, stack);
    }

    @Override
    public String toString() {
        return "BasicAttachment{" +
            "key=" + key +
            ", stack=" + stack +
            '}';
    }
}
