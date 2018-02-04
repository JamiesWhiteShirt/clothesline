package com.jamieswhiteshirt.clothesline.core;

import com.jamieswhiteshirt.clothesline.api.IActivityMovement;
import com.jamieswhiteshirt.clothesline.core.event.GetMouseOverEvent;
import com.jamieswhiteshirt.clothesline.core.event.ClientStoppedUsingItemEvent;
import com.jamieswhiteshirt.clothesline.core.event.RenderEntitiesEvent;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientHooks {
    @CapabilityInject(IActivityMovement.class)
    private static final Capability<IActivityMovement> ACTIVITY_MOVEMENT_CAPABILITY = null;

    public static void onGetMouseOver(float partialTicks) {
        MinecraftForge.EVENT_BUS.post(new GetMouseOverEvent(partialTicks));
    }

    public static void onRenderEntities(ICamera camera, float partialTicks) {
        MinecraftForge.EVENT_BUS.post(new RenderEntitiesEvent(camera, partialTicks));
    }

    public static boolean onStoppedUsingItem() {
        return MinecraftForge.EVENT_BUS.post(new ClientStoppedUsingItemEvent());
    }

    public static boolean isActivityPreventingMovement(EntityPlayerSP player) {
        if (player.isHandActive()) {
            ItemStack activeItemStack = player.getActiveItemStack();
            IActivityMovement activityMovement = activeItemStack.getCapability(ACTIVITY_MOVEMENT_CAPABILITY, null);
            return activityMovement == null || activityMovement.preventsMovement(player);
        }
        return false;
    }
}
