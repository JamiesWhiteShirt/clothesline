package com.jamieswhiteshirt.clothesline.common.tileentity;

import com.jamieswhiteshirt.clothesline.api.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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
    private static final Capability<INetworkManager> NETWORK_MANAGER_CAPABILITY = null;
    @CapabilityInject(IItemHandler.class)
    private static final Capability<IItemHandler> ITEM_HANDLER_CAPABILITY = null;
    private INetworkManager manager;

    @Nullable
    public Network getNetwork() {
        if (manager != null) {
            return manager.getNetworkByBlockPos(pos);
        } else {
            return null;
        }
    }

    public void crank(int amount) {
        Network network = getNetwork();
        if (network != null) {
            manager.addMomentum(network, amount);
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
        if (capability == ITEM_HANDLER_CAPABILITY) {
            Network network = getNetwork();
            if (network != null) {
                AbsoluteNetworkState state = network.getState();
                AbsoluteTree tree = state.getSubTree(pos);
                return ITEM_HANDLER_CAPABILITY.cast(new IItemHandler() {
                    @Override
                    public int getSlots() {
                        return tree.getChildren().size();
                    }

                    @Override
                    public ItemStack getStackInSlot(int slot) {
                        return state.getItem(tree.getChildren().get(slot).getMinOffset());
                    }

                    @Override
                    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
                        return manager.insertItem(network, tree.getEdges().get(slot).getPreMinOffset(), stack, simulate);
                    }

                    @Override
                    public ItemStack extractItem(int slot, int amount, boolean simulate) {
                        return manager.extractItem(network, tree.getChildren().get(slot).getMinOffset(), simulate);
                    }

                    @Override
                    public int getSlotLimit(int slot) {
                        return 1;
                    }
                });
            }
        }
        return null;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if (capability == ITEM_HANDLER_CAPABILITY) {
            Network network = getNetwork();
            if (network != null) {
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
}
