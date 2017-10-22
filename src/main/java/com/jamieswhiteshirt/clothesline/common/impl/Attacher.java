package com.jamieswhiteshirt.clothesline.common.impl;

import com.jamieswhiteshirt.clothesline.api.IAttacher;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

public class Attacher implements IAttacher {
    private BlockPos attachPosition;

    @Override
    @Nullable
    public BlockPos getAttachPosition() {
        return attachPosition;
    }

    @Override
    public void setAttachPosition(@Nullable BlockPos pos) {
        attachPosition = pos;
    }
}
