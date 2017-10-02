package com.jamieswhiteshirt.clothesline.common.tileentity;

import com.jamieswhiteshirt.clothesline.api.Network;
import com.jamieswhiteshirt.clothesline.api.INetworkManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.UUID;

public class TileEntityClotheslineAnchor extends TileEntity implements ITickable {
    @CapabilityInject(INetworkManager.class)
    private static final Capability<INetworkManager> NETWORK_MANAGER_CAPABILITY = null;
    private INetworkManager manager;
    @Nullable
    private UUID networkUuid;
    private Network network;

    @Nullable
    public Network getNetwork() {
        return network;
    }

    public void setNetwork(@Nullable Network network) {
        if (network != null) {
            this.network = network;
            this.networkUuid = network.getUuid();
        } else {
            this.network = null;
            this.networkUuid = null;
        }
    }

    public void crank(int amount) {
        if (network != null) {
            network.addMomentum(amount);
        }
    }

    @Override
    public void setWorld(World world) {
        super.setWorld(world);
        manager = world.getCapability(NETWORK_MANAGER_CAPABILITY, null);
        if (manager != null && networkUuid != null) {
            setNetwork(manager.getNetworkByUUID(networkUuid));
        } else {
            setNetwork(null);
        }
    }

    @Override
    public void update() {
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        if (networkUuid != null) {
            NBTTagCompound networkTag = new NBTTagCompound();
            networkTag.setUniqueId("Id", networkUuid);
            compound.setTag("Network", networkTag);
        }
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        if (compound.hasKey("Network", Constants.NBT.TAG_COMPOUND)) {
            NBTTagCompound networkTag = compound.getCompoundTag("Network");
            networkUuid = networkTag.getUniqueId("Id");
        }
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(pos, 0, getUpdateTag());
    }
}
