package com.jamieswhiteshirt.clothesline.common.capability;

import com.jamieswhiteshirt.clothesline.internal.IConnector;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;

public class ConnectorStorage implements Capability.IStorage<IConnector> {
    @Nullable
    @Override
    public NBTBase writeNBT(Capability<IConnector> capability, IConnector instance, EnumFacing side) {
        NBTTagCompound compound = new NBTTagCompound();
        BlockPos attachPosition = instance.getPos();
        if (attachPosition != null) {
            compound.setTag("Position", NBTUtil.createPosTag(attachPosition));
        }
        return compound;
    }

    @Override
    public void readNBT(Capability<IConnector> capability, IConnector instance, EnumFacing side, NBTBase nbt) {
        if (nbt instanceof NBTTagCompound) {
            NBTTagCompound compound = (NBTTagCompound) nbt;
            if (compound.hasKey("Position", Constants.NBT.TAG_COMPOUND)) {
                instance.setPos(NBTUtil.getPosFromTag(compound.getCompoundTag("Position")));
            } else {
                instance.setPos(null);
            }
        }
    }
}
