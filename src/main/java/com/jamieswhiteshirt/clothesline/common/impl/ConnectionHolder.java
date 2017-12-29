package com.jamieswhiteshirt.clothesline.common.impl;

import com.jamieswhiteshirt.clothesline.api.IConnectionHolder;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

public class ConnectionHolder implements IConnectionHolder {
    private BlockPos fromPos;

    @Override
    @Nullable
    public BlockPos getFromPos() {
        return fromPos;
    }

    @Override
    public void setFromPos(@Nullable BlockPos pos) {
        fromPos = pos;
    }
}
