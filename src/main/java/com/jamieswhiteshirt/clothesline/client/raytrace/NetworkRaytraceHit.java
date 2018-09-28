package com.jamieswhiteshirt.clothesline.client.raytrace;

import com.jamieswhiteshirt.clothesline.api.INetworkEdge;
import com.jamieswhiteshirt.clothesline.api.INetworkManager;
    import com.jamieswhiteshirt.clothesline.client.renderer.RenderClotheslineNetwork;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class NetworkRaytraceHit {
    public final double distanceSq;
    public final INetworkEdge edge;

    public NetworkRaytraceHit(double distanceSq, INetworkEdge edge) {
        this.distanceSq = distanceSq;
        this.edge = edge;
    }

    public abstract boolean hitByEntity(INetworkManager manager, EntityPlayer player);

    public abstract boolean useItem(INetworkManager manager, EntityPlayer player, EnumHand hand);

    public abstract void renderHighlight(RenderClotheslineNetwork renderClotheslineNetwork, float partialTicks, double x, double y, double z, float r, float g, float b, float a);
}
