package com.jamieswhiteshirt.clothesline.common.tileentity;

import com.jamieswhiteshirt.clothesline.api.*;
import com.jamieswhiteshirt.clothesline.common.Util;
import com.jamieswhiteshirt.clothesline.common.impl.NetworkItemHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

public class TileEntityClotheslineAnchor extends TileEntity implements ITickable {
    @CapabilityInject(INetworkManager.class)
    private static final Capability<INetworkManager> NETWORK_MANAGER_CAPABILITY = Util.nonNullInjected();
    @CapabilityInject(IItemHandler.class)
    private static final Capability<IItemHandler> ITEM_HANDLER_CAPABILITY = Util.nonNullInjected();
    private INetworkManager manager;

    @Nullable
    public INetworkManager.INetworkNode getNetworkNode() {
        if (manager != null) {
            return manager.getNetworkNodeByPos(pos);
        } else {
            return null;
        }
    }

    public void crank(int amount) {
        INetworkManager.INetworkNode node = getNetworkNode();
        if (node != null) {
            manager.addMomentum(node.getNetwork(), amount);
        }
    }

    @Override
    public void setWorld(World world) {
        super.setWorld(world);
        manager = world.getCapability(NETWORK_MANAGER_CAPABILITY, null);
    }

    @Override
    public void update() {
        //crank(1);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == ITEM_HANDLER_CAPABILITY && facing != null) {
            INetworkManager.INetworkNode node = getNetworkNode();
            if (node != null) {
                Network network = node.getNetwork();
                EdgeKey edgeKey = new EdgeKey(pos, pos.offset(facing));
                AbsoluteNetworkState state = network.getState();
                int offset = Math.floorMod(node.getGraphNode().getCornerOffset(edgeKey) - state.getOffset(), state.getLoopLength());
                return ITEM_HANDLER_CAPABILITY.cast(new NetworkItemHandler(manager, network, offset));
            }
        }
        return null;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if (capability == ITEM_HANDLER_CAPABILITY && facing != null) {
            INetworkManager.INetworkNode node = getNetworkNode();
            if (node != null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        /*if (networkUuid != null) {
            NBTTagCompound networkTag = new NBTTagCompound();
            networkTag.setUniqueId("Id", networkUuid);
            compound.setTag("Network", networkTag);
        }*/
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        /*if (compound.hasKey("Network", Constants.NBT.TAG_COMPOUND)) {
            NBTTagCompound networkTag = compound.getCompoundTag("Network");
            networkUuid = networkTag.getUniqueId("Id");
        }*/
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

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
    }
}
