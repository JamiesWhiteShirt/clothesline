package com.jamieswhiteshirt.clothesline.common.capability;

import com.jamieswhiteshirt.clothesline.api.INetworkManager;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class NetworkManagerStorage implements Capability.IStorage<INetworkManager> {
    @Nullable
    @Override
    public NBTBase writeNBT(Capability<INetworkManager> capability, INetworkManager instance, EnumFacing side) {
        return new NBTTagList();
    }

    @Override
    public void readNBT(Capability<INetworkManager> capability, INetworkManager instance, EnumFacing side, NBTBase nbt) {
    }
}
