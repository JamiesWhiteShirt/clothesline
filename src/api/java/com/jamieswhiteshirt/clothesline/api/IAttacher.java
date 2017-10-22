package com.jamieswhiteshirt.clothesline.api;

import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

public interface IAttacher {
    @Nullable
    BlockPos getAttachPosition();

    void setAttachPosition(@Nullable BlockPos pos);
}
