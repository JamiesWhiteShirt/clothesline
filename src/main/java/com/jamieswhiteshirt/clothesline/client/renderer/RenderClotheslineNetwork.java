package com.jamieswhiteshirt.clothesline.client.renderer;

import com.jamieswhiteshirt.clothesline.Clothesline;
import com.jamieswhiteshirt.clothesline.api.*;
import com.jamieswhiteshirt.clothesline.api.util.MutableSortedIntMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SideOnly(Side.CLIENT)
public class RenderClotheslineNetwork {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Clothesline.MODID, "textures/misc/clothesline.png");
    private static final VertexFormat VERTEX_FORMAT = new VertexFormat()
            .addElement(DefaultVertexFormats.POSITION_3F)
            .addElement(DefaultVertexFormats.NORMAL_3B)
            .addElement(DefaultVertexFormats.TEX_2F)
            .addElement(DefaultVertexFormats.TEX_2S);
    private static final double[] RIGHT_MULTIPLIERS = new double[] { -1.0D, -1.0D, 1.0D, 1.0D, -1.0D };
    private static final double[] UP_MULTIPLIERS = new double[] { -1.0D, 1.0D, 1.0D, -1.0D, -1.0D };
    private static final double[] NORMAL_RIGHT_MULTIPLIERS = new double[] { -1.0D, 0.0D, 1.0D, 0.0D };
    private static final double[] NORMAL_UP_MULTIPLIERS = new double[] { 0.0D, 1.0D, 0.0D, -1.0D };

    private final RenderManager renderManager;
    private final RenderItem renderItem;

    public RenderClotheslineNetwork(RenderManager renderManager, RenderItem renderItem) {
        this.renderManager = renderManager;
        this.renderItem = renderItem;
    }

    private static BufferBuilder pos(BufferBuilder bufferBuilder, Vec3d pos) {
        return bufferBuilder.pos(pos.x, pos.y, pos.z);
    }

    private static BufferBuilder posNormal(BufferBuilder bufferBuilder, Vec3d pos, Vec3d normal) {
        return bufferBuilder.pos(pos.x, pos.y, pos.z).normal((float)normal.x, (float)normal.y, (float)normal.z);
    }

    private void renderEdge(IBlockAccess world, RenderEdge e, double x, double y, double z, double networkOffset, BufferBuilder bufferBuilder) {
        for (int j = 0; j < 4; j++) {
            double r1 = RIGHT_MULTIPLIERS[j];
            double r2 = RIGHT_MULTIPLIERS[j + 1];
            double u1 = UP_MULTIPLIERS[j];
            double u2 = UP_MULTIPLIERS[j + 1];
            double nr = NORMAL_RIGHT_MULTIPLIERS[j];
            double ur = NORMAL_UP_MULTIPLIERS[j];

            double vFrom = (e.getFromOffset() - networkOffset) / Measurements.UNIT_LENGTH;
            double vTo = (e.getToOffset() - networkOffset) / Measurements.UNIT_LENGTH;

            int combinedLightFrom = world.getCombinedLight(e.getFromPos(), 0);
            int lightFrom1 = combinedLightFrom >> 16 & 0xFFFF;
            int lightFrom2 = combinedLightFrom & 0xFFFF;
            int combinedLightTo = world.getCombinedLight(e.getToPos(), 0);
            int lightTo1 = combinedLightTo >> 16 & 0xFFFF;
            int lightTo2 = combinedLightTo & 0xFFFF;

            Vec3d normal = e.projectTangent(nr, ur);
            posNormal(bufferBuilder, e.projectVec(new Vec3d(
                    (r1 - 4.0D) / 32.0D,
                    u1 / 32.0D,
                    0.0D
            )), normal).tex(0.0D, vFrom).lightmap(lightFrom1, lightFrom2).endVertex();
            posNormal(bufferBuilder, e.projectVec(new Vec3d(
                    (r2 - 4.0D) / 32.0D,
                    u2 / 32.0D,
                    0.0D
            )), normal).tex(1.0D, vFrom).lightmap(lightFrom1, lightFrom2).endVertex();
            posNormal(bufferBuilder, e.projectVec(new Vec3d(
                    (r2 - 4.0D) / 32.0D,
                    u2 / 32.0D,
                    1.0D
            )), normal).tex(1.0D, vTo).lightmap(lightTo1, lightTo2).endVertex();
            posNormal(bufferBuilder, e.projectVec(new Vec3d(
                    (r1 - 4.0D) / 32.0D,
                    u1 / 32.0D,
                    1.0D
            )), normal).tex(0.0D, vTo).lightmap(lightTo1, lightTo2).endVertex();
        }
    }

    public void render(IBlockAccess world, RenderNetworkState state, double x, double y, double z, float partialTicks) {
        Vec3d viewPos = new Vec3d(x, y, z);

        double networkOffset = state.getOffset(partialTicks);

        List<RenderEdge> renderEdges = state.getEdges();

        renderManager.renderEngine.bindTexture(TEXTURE);
        RenderHelper.enableStandardItemLighting();
        Minecraft.getMinecraft().entityRenderer.enableLightmap();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();

        GlStateManager.pushMatrix();
        GlStateManager.translate(-x, -y, -z);
        bufferBuilder.begin(GL11.GL_QUADS, VERTEX_FORMAT);
        for (RenderEdge edge : renderEdges) {
            renderEdge(world, edge, x, y, z, networkOffset, bufferBuilder);
        }
        tessellator.draw();
        GlStateManager.popMatrix();

        double speedRatio = state.getMomentum(partialTicks) / AbsoluteNetworkState.MAX_MOMENTUM;

        for (MutableSortedIntMap.Entry<ItemStack> entry : state.getStacks().entries()) {
            double attachmentOffset = (entry.getKey() + networkOffset) % state.getStacks().getMaxKey();
            int edgeIndex = state.getMinNodeIndexForOffset((int)attachmentOffset);
            RenderEdge edge = renderEdges.get(edgeIndex);
            double offsetOnEdge = attachmentOffset - edge.getFromOffset();
            double edgePosScalar = offsetOnEdge / (edge.getToOffset() - edge.getFromOffset());
            Vec3d pos = edge.projectVec(new Vec3d(-2.0D / 16.0D, 0.0D, edgePosScalar));

            RenderEdge previousEdge = renderEdges.get(Math.floorMod(edgeIndex - 1, renderEdges.size()));
            float angleDiff = (edge.getAngleY() - previousEdge.getAngleY() + 360.0F) % 360.0F;

            GlStateManager.pushMatrix();
            GlStateManager.translate(pos.x - viewPos.x, pos.y - viewPos.y, pos.z - viewPos.z);
            GlStateManager.scale(0.5D, 0.5D, 0.5D);
            GlStateManager.rotate(-edge.getAngleY(), 0.0f, 1.0f, 0.0f);
            GlStateManager.rotate(
                    (angleDiff / 4.0F) *
                    (float)(speedRatio * speedRatio) *
                    (float)(Math.exp(-offsetOnEdge / (Measurements.UNIT_LENGTH * 2.0D))) *
                    MathHelper.sin((float)(offsetOnEdge / (AbsoluteNetworkState.MAX_MOMENTUM * 2.0D)))
                    , 1.0F, 0.0F, 0.0F);
            GlStateManager.translate(0.0f, -0.5f, 0.0f);

            renderItem.renderItem(entry.getValue(), ItemCameraTransforms.TransformType.FIXED);

            GlStateManager.popMatrix();
        }

        Minecraft.getMinecraft().entityRenderer.disableLightmap();
    }

    public void renderOutline(RenderEdge edge, double x, double y, double z) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);

        for (int i = 0; i < 4; i++) {
            double up = (UP_MULTIPLIERS[i] * 1.01D) / 32.0D;
            double right = (RIGHT_MULTIPLIERS[i] * 1.01D - 4.0D) / 32.0D;

            pos(bufferBuilder, edge.projectVec(new Vec3d(right, up, 0.0D)).subtract(x, y, z)).color(0, 0, 0, 255).endVertex();
            pos(bufferBuilder, edge.projectVec(new Vec3d(right, up, 1.0D)).subtract(x, y, z)).color(0, 0, 0, 255).endVertex();
        }

        tessellator.draw();
    }

    private void debugRenderText(String msg, double x, double y, double z, float yaw, float pitch, FontRenderer fontRenderer) {
        EntityRenderer.drawNameplate(fontRenderer, msg, (float)x, (float)y, (float)z, 0, yaw, pitch, false, false);
    }

    private void debugRenderTree(AbsoluteTree absoluteTree, double x, double y, double z, float yaw, float pitch, FontRenderer fontRenderer) {
        List<AbsoluteTree> children = absoluteTree.getChildren();
        for (int i = 0; i < children.size(); i++) {
            AbsoluteTree child = children.get(i);
            Vec3d pos = new Vec3d(absoluteTree.getPos()).scale(0.75D).add(new Vec3d(child.getPos()).scale(0.25D)).addVector(0.5D, 0.5D, 0.5D);

            debugRenderText(Integer.toString(i), pos.x - x, pos.y - y, pos.z - z, yaw, pitch, fontRenderer);

            debugRenderTree(child, x, y, z, yaw, pitch, fontRenderer);
        }
    }

    public void debugRender(RenderNetworkState state, double x, double y, double z, float partialTicks) {
        TileEntityRendererDispatcher rendererDispatcher = TileEntityRendererDispatcher.instance;
        float yaw = rendererDispatcher.entityYaw;
        float pitch = rendererDispatcher.entityPitch;
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;

        List<RenderEdge> renderEdges = state.getEdges();
        for (int i = 0; i < renderEdges.size(); i++) {
            RenderEdge edge = renderEdges.get(i);
            Vec3d pos = edge.projectVec(new Vec3d(0.125D, 0.125D, 0.5D));
            debugRenderText(i + ": " + edge.getAngleY(), pos.x - x, pos.y - y, pos.z - z, yaw, pitch, fontRenderer);
        }

        debugRenderTree(state.getTree(), x, y, z, yaw, pitch, fontRenderer);

        double networkOffset = state.getOffset(partialTicks);
        for (MutableSortedIntMap.Entry<ItemStack> entry : state.getStacks().entries()) {
            double attachmentOffset = (entry.getKey() + networkOffset) % state.getStacks().getMaxKey();
            int edgeIndex = state.getMinNodeIndexForOffset((int)attachmentOffset);
            RenderEdge edge = renderEdges.get(edgeIndex);
            double offsetOnEdge = attachmentOffset - edge.getFromOffset();
            double edgePosScalar = offsetOnEdge / (edge.getToOffset() - edge.getFromOffset());
            Vec3d pos = edge.projectVec(new Vec3d(-2.0D / 16.0D, 0.0D, edgePosScalar));
            debugRenderText(Integer.toString(entry.getKey()), pos.x - x, pos.y - y, pos.z - z, yaw, pitch, fontRenderer);
        }
    }
}
