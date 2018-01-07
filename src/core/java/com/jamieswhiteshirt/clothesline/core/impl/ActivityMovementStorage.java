package com.jamieswhiteshirt.clothesline.core.impl;

import com.jamieswhiteshirt.clothesline.api.IActivityMovement;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class ActivityMovementStorage implements Capability.IStorage<IActivityMovement> {
    @Nullable
    @Override
    public NBTBase writeNBT(Capability<IActivityMovement> capability, IActivityMovement instance, EnumFacing side) {
        return null;
    }

    @Override
    public void readNBT(Capability<IActivityMovement> capability, IActivityMovement instance, EnumFacing side, NBTBase nbt) { }
}
