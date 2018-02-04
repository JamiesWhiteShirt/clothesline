package com.jamieswhiteshirt.clothesline.client.entity;

import com.jamieswhiteshirt.clothesline.api.client.IClientNetworkManager;
import com.jamieswhiteshirt.clothesline.client.raytrace.NetworkRaytraceHit;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class EntityNetworkRaytraceHit extends Entity {
    private IClientNetworkManager manager;
    private NetworkRaytraceHit hit;

    public EntityNetworkRaytraceHit(World worldIn) {
        super(worldIn);
    }

    public EntityNetworkRaytraceHit(World world, IClientNetworkManager manager, NetworkRaytraceHit hit) {
        this(world);
        this.manager = manager;
        this.hit = hit;
    }

    @Override
    protected void entityInit() { }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) { }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) { }

    @Override
    public boolean hitByEntity(Entity entity) {
        return hit.hitByEntity(manager, (EntityPlayer) entity);
    }

    @Override
    public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
        return hit.useItem(manager, player, hand);
    }

    public NetworkRaytraceHit getHit() {
        return hit;
    }
}
