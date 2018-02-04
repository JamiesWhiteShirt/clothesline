package com.jamieswhiteshirt.clothesline.core.event;

import net.minecraft.client.renderer.culling.ICamera;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderEntitiesEvent extends Event {
    private final ICamera camera;
    private final float partialTicks;

    public RenderEntitiesEvent(ICamera camera, float partialTicks) {
        this.camera = camera;
        this.partialTicks = partialTicks;
    }

    public ICamera getCamera() {
        return camera;
    }

    public float getPartialTicks() {
        return partialTicks;
    }
}
