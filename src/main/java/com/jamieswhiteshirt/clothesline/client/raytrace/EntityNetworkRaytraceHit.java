package com.jamieswhiteshirt.clothesline.client.raytrace;

import com.jamieswhiteshirt.clothesline.api.INetworkManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class EntityNetworkRaytraceHit extends Entity {
    private INetworkManager manager;
    private NetworkRaytraceHit hit;

    public EntityNetworkRaytraceHit(World worldIn) {
        super(worldIn);
    }

    public EntityNetworkRaytraceHit(World world, INetworkManager manager, NetworkRaytraceHit hit) {
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

    @Override
    public ItemStack getPickedResult(RayTraceResult target) {
        return hit.getPickedResult();
    }

    public NetworkRaytraceHit getHit() {
        return hit;
    }
}
