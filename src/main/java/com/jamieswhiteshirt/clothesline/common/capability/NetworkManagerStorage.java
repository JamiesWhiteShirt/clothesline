package com.jamieswhiteshirt.clothesline.common.capability;

import com.jamieswhiteshirt.clothesline.api.ICommonNetworkManager;
import com.jamieswhiteshirt.clothesline.common.util.BasicNetwork;
import com.jamieswhiteshirt.clothesline.common.util.NBTSerialization;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;
import java.util.stream.Collectors;

public class NetworkManagerStorage implements Capability.IStorage<ICommonNetworkManager> {
    @Nullable
    @Override
    public NBTBase writeNBT(Capability<ICommonNetworkManager> capability, ICommonNetworkManager instance, EnumFacing side) {
        return NBTSerialization.writeNetworks(instance.getNetworks().stream().map(
                BasicNetwork::fromAbsolute
        ).collect(Collectors.toList()));
    }

    @Override
    public void readNBT(Capability<ICommonNetworkManager> capability, ICommonNetworkManager instance, EnumFacing side, NBTBase nbt) {
        if (nbt instanceof NBTTagList) {
            instance.setNetworks(NBTSerialization.readNetworks((NBTTagList)nbt).stream().map(
                    BasicNetwork::toAbsolute
            ).collect(Collectors.toList()));
        }
    }
}
