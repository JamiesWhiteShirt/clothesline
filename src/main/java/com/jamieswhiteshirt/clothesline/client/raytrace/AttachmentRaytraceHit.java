package com.jamieswhiteshirt.clothesline.client.raytrace;

import com.jamieswhiteshirt.clothesline.Clothesline;
import com.jamieswhiteshirt.clothesline.api.Network;
import com.jamieswhiteshirt.clothesline.api.client.IClientNetworkEdge;
import com.jamieswhiteshirt.clothesline.api.client.IClientNetworkManager;
import com.jamieswhiteshirt.clothesline.client.renderer.RenderClotheslineNetwork;
import com.jamieswhiteshirt.clothesline.common.network.message.MessageHitAttachment;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.util.vector.Matrix4f;

import java.nio.FloatBuffer;

@SideOnly(Side.CLIENT)
public class AttachmentRaytraceHit extends NetworkRaytraceHit {
    private static final AxisAlignedBB attachmentBox = new AxisAlignedBB(-0.5D, -0.5D, -0.5D, 0.5D, 0.5D, 0.5D);
    private final int attachmentKey;
    private final Matrix4f l2w;

    public AttachmentRaytraceHit(double distanceSq, IClientNetworkEdge edge, int attachmentKey, Matrix4f l2w) {
        super(distanceSq, edge);
        this.attachmentKey = attachmentKey;
        this.l2w = l2w;
    }

    @Override
    public boolean hitByEntity(IClientNetworkManager manager, EntityPlayer player) {
        Network network = edge.getNetwork();
        Clothesline.instance.networkWrapper.sendToServer(new MessageHitAttachment(network.getId(), attachmentKey));
        manager.hitAttachment(network, player, attachmentKey);
        return true;
    }

    @Override
    public boolean useItem(IClientNetworkManager manager, EntityPlayer player, EnumHand hand) {
        return false;
    }

    @Override
    public void renderHighlight(RenderClotheslineNetwork renderClotheslineNetwork, float partialTicks, double x, double y, double z, float r, float g, float b, float a) {
        FloatBuffer l2wBuffer = GLAllocation.createDirectFloatBuffer(16);
        l2w.store(l2wBuffer);
        l2wBuffer.flip();

        GlStateManager.pushMatrix();
        GlStateManager.translate(-x, -y, -z);
        GlStateManager.multMatrix(l2wBuffer);
        RenderGlobal.drawSelectionBoundingBox(attachmentBox, r, g, b, a);
        GlStateManager.popMatrix();
    }
}
