package com.jamieswhiteshirt.clothesline.client.renderer.tileentity;

import com.jamieswhiteshirt.clothesline.api.INetworkManager;
import com.jamieswhiteshirt.clothesline.api.Measurements;
import com.jamieswhiteshirt.clothesline.api.Network;
import com.jamieswhiteshirt.clothesline.common.ClotheslineItems;
import com.jamieswhiteshirt.clothesline.common.block.BlockClotheslineAnchor;
import com.jamieswhiteshirt.clothesline.common.tileentity.TileEntityClotheslineAnchor;
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
        INetworkManager.INetworkNode node = te.getNetworkNode();
        float crankRotation = 0.0F;
        if (node != null) {
            Network network = node.getNetwork();
            float shift = network.getState().getShift() * partialTicks + network.getState().getPreviousShift() * (1.0F - partialTicks);
            crankRotation = -shift * 360.0F / Measurements.UNIT_LENGTH;
        }
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
        if (te.getWorld().getBlockState(te.getPos()).getValue(BlockClotheslineAnchor.FACING) == EnumFacing.DOWN) {
            GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
            crankRotation = -crankRotation;
        }
        GlStateManager.rotate(crankRotation, 0.0F, 1.0F, 0.0F);

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0D, 4.0D / 16.0D, 0.0D);
        renderItem.renderItem(new ItemStack(ClotheslineItems.CLOTHESLINE_CRANK), ItemCameraTransforms.TransformType.FIXED);
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.scale(2.0D, 2.0D, 2.0D);
        renderItem.renderItem(new ItemStack(ClotheslineItems.PULLEY_WHEEL), ItemCameraTransforms.TransformType.FIXED);
        GlStateManager.popMatrix();

        GlStateManager.popMatrix();
    }
}
