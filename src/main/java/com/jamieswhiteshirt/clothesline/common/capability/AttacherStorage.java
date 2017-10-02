package com.jamieswhiteshirt.clothesline.common.capability;

import com.jamieswhiteshirt.clothesline.api.IAttacher;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;

public class AttacherStorage implements Capability.IStorage<IAttacher> {
    @Nullable
    @Override
    public NBTBase writeNBT(Capability<IAttacher> capability, IAttacher instance, EnumFacing side) {
        NBTTagCompound compound = new NBTTagCompound();
        BlockPos attachPosition = instance.getAttachPosition();
        if (attachPosition != null) {
            NBTTagCompound attachPositionCompound = new NBTTagCompound();
            attachPositionCompound.setInteger("x", attachPosition.getX());
            attachPositionCompound.setInteger("y", attachPosition.getY());
            attachPositionCompound.setInteger("z", attachPosition.getZ());
            compound.setTag("AttachPosition", attachPositionCompound);
        }
        return compound;
    }

    @Override
    public void readNBT(Capability<IAttacher> capability, IAttacher instance, EnumFacing side, NBTBase nbt) {
        if (nbt instanceof NBTTagCompound) {
            NBTTagCompound compound = (NBTTagCompound) nbt;
            if (compound.hasKey("AttachPosition", Constants.NBT.TAG_COMPOUND)) {
                NBTTagCompound attachPositionCompound = compound.getCompoundTag("AttachPosition");
                instance.setAttachPosition(new BlockPos(
                        attachPositionCompound.getInteger("x"),
                        attachPositionCompound.getInteger("y"),
                        attachPositionCompound.getInteger("z")
                ));
            }
        }
    }
}
