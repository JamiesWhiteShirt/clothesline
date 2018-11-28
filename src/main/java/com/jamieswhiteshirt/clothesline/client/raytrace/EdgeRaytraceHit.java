package com.jamieswhiteshirt.clothesline.client.raytrace;

import com.jamieswhiteshirt.clothesline.Clothesline;
import com.jamieswhiteshirt.clothesline.api.INetwork;
import com.jamieswhiteshirt.clothesline.api.INetworkEdge;
import com.jamieswhiteshirt.clothesline.api.INetworkManager;
import com.jamieswhiteshirt.clothesline.client.LineProjection;
import com.jamieswhiteshirt.clothesline.client.renderer.RenderClotheslineNetwork;
import com.jamieswhiteshirt.clothesline.common.ClotheslineItems;
import com.jamieswhiteshirt.clothesline.common.network.message.HitNetworkMessage;
import com.jamieswhiteshirt.clothesline.common.network.message.TryUseItemOnNetworkMessage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EdgeRaytraceHit extends NetworkRaytraceHit {
    public final double offset;

    public EdgeRaytraceHit(double distanceSq, INetworkEdge edge, double offset) {
        super(distanceSq, edge);
        this.offset = offset;
    }

    @Override
    public boolean hitByEntity(INetworkManager manager, EntityPlayer player) {
        int offset = (int) Math.round(this.offset);
        INetwork network = edge.getNetwork();
        int attachmentKey = network.getState().offsetToAttachmentKey(offset);
        Vec3d pos = edge.getPathEdge().getPositionForOffset(offset);
        player.world.playSound(player, pos.x, pos.y, pos.z, SoundEvents.ENTITY_LEASHKNOT_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F);
        Clothesline.instance.networkChannel.sendToServer(new HitNetworkMessage(network.getId(), attachmentKey, offset));
        return true;
    }

    @Override
    public boolean useItem(INetworkManager manager, EntityPlayer player, EnumHand hand) {
        int offset = (int) Math.round(this.offset);
        INetwork network = edge.getNetwork();
        int attachmentKey = network.getState().offsetToAttachmentKey(offset);
        Clothesline.instance.networkChannel.sendToServer(new TryUseItemOnNetworkMessage(hand, network.getId(), attachmentKey));
        return network.useItem(player, hand, attachmentKey);
    }

    @Override
    public void renderHighlight(RenderClotheslineNetwork renderClotheslineNetwork, float partialTicks, double x, double y, double z, float r, float g, float b, float a) {
        renderClotheslineNetwork.renderOutline(LineProjection.create(edge), x, y, z, r, g, b, a);
    }

    @Override
    public ItemStack getPickedResult() {
        return new ItemStack(ClotheslineItems.CLOTHESLINE);
    }
}
