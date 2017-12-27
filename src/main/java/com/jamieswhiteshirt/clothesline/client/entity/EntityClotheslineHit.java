package com.jamieswhiteshirt.clothesline.client.entity;

import com.jamieswhiteshirt.clothesline.Clothesline;
import com.jamieswhiteshirt.clothesline.api.INetworkManager;
import com.jamieswhiteshirt.clothesline.api.Network;
import com.jamieswhiteshirt.clothesline.client.renderer.RenderEdge;
import com.jamieswhiteshirt.clothesline.common.network.message.MessageHitNetwork;
import com.jamieswhiteshirt.clothesline.common.network.message.MessageTryUseItemOnNetwork;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EntityClotheslineHit extends Entity {
    private INetworkManager manager;
    private Network network;
    private RenderEdge edge;
    private double offset;

    public EntityClotheslineHit(World world) {
        super(world);
    }

    public EntityClotheslineHit(World world, INetworkManager manager, Network network, RenderEdge edge, double offset) {
        this(world);
        this.manager = manager;
        this.network = network;
        this.edge = edge;
        this.offset = offset;
    }

    @Override
    protected void entityInit() { }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) { }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) { }

    @Override
    public boolean hitByEntity(Entity entityIn) {
        Clothesline.instance.networkWrapper.sendToServer(new MessageHitNetwork(edge.getFromPos(), edge.getToPos()));
        return true;
    }

    @Override
    public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
        int offset = (int)this.offset - network.getState().getOffset();
        Clothesline.instance.networkWrapper.sendToServer(new MessageTryUseItemOnNetwork(hand, network.getUuid(), offset));
        return manager.useItem(network, player, hand, offset);
    }

    public RenderEdge getEdge() {
        return edge;
    }

    public double getOffset() {
        return offset;
    }
}
