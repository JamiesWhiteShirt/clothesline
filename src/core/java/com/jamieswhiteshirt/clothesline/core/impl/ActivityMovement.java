package com.jamieswhiteshirt.clothesline.core.impl;

import com.jamieswhiteshirt.clothesline.api.IActivityMovement;
import net.minecraft.entity.player.EntityPlayer;

public class ActivityMovement implements IActivityMovement {
    @Override
    public boolean preventsMovement(EntityPlayer player) {
        return true;
    }
}
