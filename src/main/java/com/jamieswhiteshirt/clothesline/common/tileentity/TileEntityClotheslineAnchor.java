package com.jamieswhiteshirt.clothesline.common.tileentity;

import com.jamieswhiteshirt.clothesline.Clothesline;
import com.jamieswhiteshirt.clothesline.api.*;
import com.jamieswhiteshirt.clothesline.common.Util;
import com.jamieswhiteshirt.clothesline.common.impl.NetworkItemHandler;
import com.jamieswhiteshirt.clothesline.common.network.message.SetAnchorHasCrankMessage;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

public class TileEntityClotheslineAnchor extends TileEntity implements ITickable {
    @CapabilityInject(IItemHandler.class)
    private static final Capability<IItemHandler> ITEM_HANDLER_CAPABILITY = Util.nonNullInjected();
    private INetworkManager<?, ?> manager;
    private boolean hasCrank;

    public boolean getHasCrank() {
        return hasCrank;
    }

    public void setHasCrank(boolean hasCrank) {
        this.hasCrank = hasCrank;
        if (!world.isRemote) {
            Clothesline.instance.networkChannel.sendToAllTracking(
                new SetAnchorHasCrankMessage(pos, hasCrank),
                new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 0)
            );
        }
    }


    @Nullable
    public INetworkNode getNetworkNode() {
        if (manager != null) {
            return manager.getNodes().get(pos);
        } else {
            return null;
        }
    }

    public void crank(int amount) {
        INetworkNode node = getNetworkNode();
        if (node != null) {
            INetworkState networkState = node.getNetwork().getState();
            networkState.setMomentum(networkState.getMomentum() + amount);
        }
    }

    @Override
    public void setWorld(World world) {
        super.setWorld(world);
        manager = world.getCapability(Clothesline.NETWORK_MANAGER_CAPABILITY, null);
    }

    @Override
    public void update() {
        //crank(1);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == ITEM_HANDLER_CAPABILITY && facing != null) {
            INetworkNode node = getNetworkNode();
            if (node != null) {
                INetwork network = node.getNetwork();
                DeltaKey deltaKey = DeltaKey.between(pos, pos.offset(facing));
                int attachmentKey = network.getState().offsetToAttachmentKey(node.getGraphNode().getCornerOffset(deltaKey));
                return ITEM_HANDLER_CAPABILITY.cast(new NetworkItemHandler(network, attachmentKey));
            }
        }
        return null;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if (capability == ITEM_HANDLER_CAPABILITY && facing != null) {
            INetworkNode node = getNetworkNode();
            return node != null;
        }
        return false;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setBoolean("HasCrank", hasCrank);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        hasCrank = compound.getBoolean("HasCrank");
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

    @SideOnly(Side.CLIENT)
    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
    }
}
