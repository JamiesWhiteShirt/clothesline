package com.jamieswhiteshirt.clothesline.client.entity;

import com.jamieswhiteshirt.clothesline.Clothesline;
import com.jamieswhiteshirt.clothesline.common.network.message.MessageHitNetwork;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EntityClotheslineHit extends Entity {
    private BlockPos posA;
    private BlockPos posB;

    public EntityClotheslineHit(World world) {
        super(world);
    }

    public EntityClotheslineHit(World world, BlockPos posA, BlockPos posB) {
        this(world);
        this.posA = posA;
        this.posB = posB;
    }

    @Override
    protected void entityInit() { }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) { }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) { }

    @Override
    public boolean hitByEntity(Entity entityIn) {
        Clothesline.instance.networkWrapper.sendToServer(new MessageHitNetwork(posA, posB));
        return true;
    }
}
