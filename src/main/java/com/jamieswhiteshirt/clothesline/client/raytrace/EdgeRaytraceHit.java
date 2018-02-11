package com.jamieswhiteshirt.clothesline.client.raytrace;

import com.jamieswhiteshirt.clothesline.Clothesline;
import com.jamieswhiteshirt.clothesline.api.Network;
import com.jamieswhiteshirt.clothesline.api.client.IClientNetworkEdge;
import com.jamieswhiteshirt.clothesline.api.client.IClientNetworkManager;
import com.jamieswhiteshirt.clothesline.client.renderer.RenderClotheslineNetwork;
import com.jamieswhiteshirt.clothesline.common.network.message.MessageHitNetwork;
import com.jamieswhiteshirt.clothesline.common.network.message.MessageTryUseItemOnNetwork;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EdgeRaytraceHit extends NetworkRaytraceHit {
    private final double offset;

    public EdgeRaytraceHit(double distanceSq, IClientNetworkEdge edge, double offset) {
        super(distanceSq, edge);
        this.offset = offset;
    }

    @Override
    public boolean hitByEntity(IClientNetworkManager manager, EntityPlayer player) {
        int offset = (int) Math.round(this.offset);
        Network network = edge.getNetwork();
        int attachmentKey = network.getState().offsetToAttachmentKey(offset);
        Clothesline.instance.networkWrapper.sendToServer(new MessageHitNetwork(network.getId(), attachmentKey, offset));
        return true;
    }

    @Override
    public boolean useItem(IClientNetworkManager manager, EntityPlayer player, EnumHand hand) {
        int offset = (int) Math.round(this.offset);
        Network network = edge.getNetwork();
        int attachmentKey = network.getState().offsetToAttachmentKey(offset);
        Clothesline.instance.networkWrapper.sendToServer(new MessageTryUseItemOnNetwork(hand, network.getId(), attachmentKey));
        return manager.useItem(network, player, hand, attachmentKey);
    }

    @Override
    public void renderHighlight(RenderClotheslineNetwork renderClotheslineNetwork, float partialTicks, double x, double y, double z, float r, float g, float b, float a) {
        renderClotheslineNetwork.renderOutline(edge.getProjection(), x, y, z, r, g, b, a);
    }
}
