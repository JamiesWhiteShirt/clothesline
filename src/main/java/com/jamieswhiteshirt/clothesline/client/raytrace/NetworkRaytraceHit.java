package com.jamieswhiteshirt.clothesline.client.raytrace;

import com.jamieswhiteshirt.clothesline.api.client.IClientNetworkEdge;
import com.jamieswhiteshirt.clothesline.api.client.IClientNetworkManager;
import com.jamieswhiteshirt.clothesline.client.renderer.RenderClotheslineNetwork;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class NetworkRaytraceHit {
    public final double distanceSq;
    public final IClientNetworkEdge edge;

    public NetworkRaytraceHit(double distanceSq, IClientNetworkEdge edge) {
        this.distanceSq = distanceSq;
        this.edge = edge;
    }

    public abstract boolean hitByEntity(IClientNetworkManager manager, EntityPlayer player);

    public abstract boolean useItem(IClientNetworkManager manager, EntityPlayer player, EnumHand hand);

    public abstract void renderHighlight(RenderClotheslineNetwork renderClotheslineNetwork, float partialTicks, double x, double y, double z, float r, float g, float b, float a);
}
