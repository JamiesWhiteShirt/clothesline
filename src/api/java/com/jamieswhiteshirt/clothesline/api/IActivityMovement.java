package com.jamieswhiteshirt.clothesline.api;

import net.minecraft.entity.player.EntityPlayer;

public interface IActivityMovement {
    boolean preventsMovement(EntityPlayer player);
}
