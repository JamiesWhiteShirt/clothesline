package com.jamieswhiteshirt.clothesline.api;

import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

public interface IConnectionHolder {
    @Nullable
    BlockPos getFromPos();

    void setFromPos(@Nullable BlockPos pos);
}
