package com.jamieswhiteshirt.clothesline.api;

import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

public interface IConnector {
    @Nullable
    BlockPos getPos();

    void setPos(@Nullable BlockPos pos);
}
