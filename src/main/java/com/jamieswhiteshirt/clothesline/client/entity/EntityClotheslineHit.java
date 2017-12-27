package com.jamieswhiteshirt.clothesline.client.entity;

import com.jamieswhiteshirt.clothesline.Clothesline;
import com.jamieswhiteshirt.clothesline.client.renderer.RenderEdge;
import com.jamieswhiteshirt.clothesline.common.network.message.MessageHitNetwork;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EntityClotheslineHit extends Entity {
    private RenderEdge edge;
    private double offset;

    public EntityClotheslineHit(World world) {
        super(world);
    }

    public EntityClotheslineHit(World world, RenderEdge edge, double offset) {
        this(world);
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
        return true;
    }

    public RenderEdge getEdge() {
        return edge;
    }

    public double getOffset() {
        return offset;
    }
}
