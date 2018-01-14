package com.jamieswhiteshirt.clothesline.common.capability;

import com.jamieswhiteshirt.clothesline.api.IServerNetworkManager;
import com.jamieswhiteshirt.clothesline.api.Network;
import com.jamieswhiteshirt.clothesline.common.util.BasicPersistentNetwork;
import com.jamieswhiteshirt.clothesline.common.util.NBTSerialization;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;
import java.util.stream.Collectors;

public class ServerNetworkManagerStorage implements Capability.IStorage<IServerNetworkManager> {
    @Nullable
    @Override
    public NBTBase writeNBT(Capability<IServerNetworkManager> capability, IServerNetworkManager instance, EnumFacing side) {
        return NBTSerialization.writePersistentNetworks(instance.getNetworks().stream().map(
                Network::getPersistent
        ).map(
                BasicPersistentNetwork::fromAbsolute
        ).collect(Collectors.toList()));
    }

    @Override
    public void readNBT(Capability<IServerNetworkManager> capability, IServerNetworkManager instance, EnumFacing side, NBTBase nbt) {
        if (nbt instanceof NBTTagList) {
            instance.reset(NBTSerialization.readPersistentNetworks((NBTTagList) nbt).stream().map(
                    BasicPersistentNetwork::toAbsolute
            ).collect(Collectors.toList()));
        }
    }
}
