package com.jamieswhiteshirt.clothesline.client.raytrace;

import com.jamieswhiteshirt.clothesline.Clothesline;
import com.jamieswhiteshirt.clothesline.api.client.IClientNetworkManager;
import com.jamieswhiteshirt.clothesline.client.raytrace.NetworkRaytraceHit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.Field;

@SideOnly(Side.CLIENT)
public final class EntityNetworkRaytraceHit extends Entity {
    private static final Field blockHitDelay = ReflectionHelper.findField(PlayerControllerMP.class, "field_78781_i", "blockHitDelay");

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
        /*PlayerControllerMP playerController = Minecraft.getMinecraft().playerController;
        try {
            int delay = (int)blockHitDelay.get(playerController);
            if (delay > 0) {
                blockHitDelay.set(playerController, delay - 1);
                return true;
            } else {
                if (hit.hitByEntity(manager, (EntityPlayer) entity)) {
                    blockHitDelay.set(playerController, 5);
                    return true;
                } else {
                    return false;
                }
            }
        } catch (IllegalAccessException e) {
            Clothesline.logger.error("Could not access block hit delay for clothesline hit", e);
            return false;
        }*/
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
