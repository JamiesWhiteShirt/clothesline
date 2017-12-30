package com.jamieswhiteshirt.clothesline.common.impl;

import com.jamieswhiteshirt.clothesline.api.IConnector;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

public class Connector implements IConnector {
    private BlockPos pos;

    @Override
    @Nullable
    public BlockPos getPos() {
        return pos;
    }

    @Override
    public void setPos(@Nullable BlockPos pos) {
        this.pos = pos;
    }
}
