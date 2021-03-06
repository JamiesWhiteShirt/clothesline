package com.jamieswhiteshirt.clothesline.client.renderer.tileentity;

import com.jamieswhiteshirt.clothesline.api.INetwork;
import com.jamieswhiteshirt.clothesline.api.INetworkNode;
import com.jamieswhiteshirt.clothesline.api.AttachmentUnit;
import com.jamieswhiteshirt.clothesline.common.ClotheslineBlocks;
import com.jamieswhiteshirt.clothesline.common.ClotheslineItems;
import com.jamieswhiteshirt.clothesline.common.block.BlockClotheslineAnchor;
import com.jamieswhiteshirt.clothesline.common.tileentity.TileEntityClotheslineAnchor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TileEntityClotheslineAnchorRenderer extends TileEntitySpecialRenderer<TileEntityClotheslineAnchor> {
    private final RenderItem renderItem;

    public TileEntityClotheslineAnchorRenderer(RenderItem renderItem) {
        this.renderItem = renderItem;
    }

    @Override
    public void render(TileEntityClotheslineAnchor te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        IBlockState state = te.getWorld().getBlockState(te.getPos());
        if (state.getBlock() != ClotheslineBlocks.CLOTHESLINE_ANCHOR) return;
        INetworkNode node = te.getNetworkNode();
        float crankRotation = 0.0F;
        if (node != null) {
            INetwork network = node.getNetwork();
            float shift = network.getState().getShift() * partialTicks + network.getState().getPreviousShift() * (1.0F - partialTicks);
            crankRotation = -(node.getPathNode().getBaseRotation() + shift) * 360.0F / AttachmentUnit.UNITS_PER_BLOCK;
        }
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
        if (state.getValue(BlockClotheslineAnchor.FACING) == EnumFacing.DOWN) {
            GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
            crankRotation = -crankRotation;
        }
        GlStateManager.rotate(crankRotation, 0.0F, 1.0F, 0.0F);

        GlStateManager.pushMatrix();
        GlStateManager.scale(2.0D, 2.0D, 2.0D);
        renderItem.renderItem(new ItemStack(ClotheslineItems.CLOTHESLINE_ANCHOR, 1, 1), ItemCameraTransforms.TransformType.FIXED);
        if (node != null && !node.getNetwork().getState().getTree().isEmpty()) {
            renderItem.renderItem(new ItemStack(ClotheslineItems.CLOTHESLINE_ANCHOR, 1, 2), ItemCameraTransforms.TransformType.FIXED);
        }
        GlStateManager.popMatrix();

        if (te.getHasCrank()) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0D, 4.0D / 16.0D, 0.0D);
            renderItem.renderItem(new ItemStack(ClotheslineItems.CRANK), ItemCameraTransforms.TransformType.FIXED);
            GlStateManager.popMatrix();
        }

        GlStateManager.popMatrix();
    }
}
