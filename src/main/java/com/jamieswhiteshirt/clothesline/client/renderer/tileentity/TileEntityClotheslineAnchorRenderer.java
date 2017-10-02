package com.jamieswhiteshirt.clothesline.client.renderer.tileentity;

import com.jamieswhiteshirt.clothesline.api.Network;
import com.jamieswhiteshirt.clothesline.common.ClotheslineItems;
import com.jamieswhiteshirt.clothesline.common.tileentity.TileEntityClotheslineAnchor;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
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
        Network network = te.getNetwork();
        float crankRotation = 0.0F;
        if (network != null) {
            crankRotation = (network.getOffset() * partialTicks + network.getPreviousOffset() * (1.0F - partialTicks)) * 4.0F;
        }
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
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
