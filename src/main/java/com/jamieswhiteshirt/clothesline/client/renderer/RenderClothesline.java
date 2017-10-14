package com.jamieswhiteshirt.clothesline.client.renderer;

import com.jamieswhiteshirt.clothesline.Clothesline;
import com.jamieswhiteshirt.clothesline.api.*;
import com.jamieswhiteshirt.clothesline.api.util.SortedIntShiftMap;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.Arrays;
import java.util.List;

@SideOnly(Side.CLIENT)
public class RenderClothesline {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Clothesline.MODID, "textures/misc/clothesline.png");
    private static final VertexFormat VERTEX_FORMAT = new VertexFormat()
            .addElement(DefaultVertexFormats.POSITION_3F)
            .addElement(DefaultVertexFormats.NORMAL_3B)
            .addElement(DefaultVertexFormats.TEX_2F);
    private static final double[] RIGHT_MULTIPLIERS = new double[] { -1.0D, -1.0D, 1.0D, 1.0D, -1.0D };
    private static final double[] UP_MULTIPLIERS = new double[] { -1.0D, 1.0D, 1.0D, -1.0D, -1.0D };
    private static final double[] NORMAL_RIGHT_MULTIPLIERS = new double[] { -1.0D, 0.0D, 1.0D, 0.0D };
    private static final double[] NORMAL_UP_MULTIPLIERS = new double[] { 0.0D, 1.0D, 0.0D, -1.0D };

    private final RenderManager renderManager;
    private final RenderItem renderItem;

    public RenderClothesline(RenderManager renderManager, RenderItem renderItem) {
        this.renderManager = renderManager;
        this.renderItem = renderItem;
    }

    private static BufferBuilder posNormal(BufferBuilder bufferBuilder, Vec3d pos, Vec3d normal) {
        return bufferBuilder.pos(pos.x, pos.y, pos.z).normal((float)normal.x, (float)normal.y, (float)normal.z);
    }

    private void renderEdge(RenderEdge e, double x, double y, double z, double networkOffset, BufferBuilder bufferBuilder) {
        for (int j = 0; j < 4; j++) {
            double r1 = RIGHT_MULTIPLIERS[j];
            double r2 = RIGHT_MULTIPLIERS[j + 1];
            double u1 = UP_MULTIPLIERS[j];
            double u2 = UP_MULTIPLIERS[j + 1];
            double nr = NORMAL_RIGHT_MULTIPLIERS[j];
            double ur = NORMAL_UP_MULTIPLIERS[j];

            Vec3d normal = e.projectTangent(nr, ur);
            posNormal(bufferBuilder, e.projectVec(new Vec3d(
                    (r1 - 4.0D) / 32.0D,
                    u1 / 32.0D,
                    0.0D
            )), normal).tex(0.0D, (e.getFromOffset() + networkOffset) / Measurements.UNIT_LENGTH).endVertex();
            posNormal(bufferBuilder, e.projectVec(new Vec3d(
                    (r2 - 4.0D) / 32.0D,
                    u2 / 32.0D,
                    0.0D
            )), normal).tex(1.0D, (e.getFromOffset() + networkOffset) / Measurements.UNIT_LENGTH).endVertex();
            posNormal(bufferBuilder, e.projectVec(new Vec3d(
                    (r2 - 4.0D) / 32.0D,
                    u2 / 32.0D,
                    1.0D
            )), normal).tex(1.0D, (e.getToOffset() + networkOffset) / Measurements.UNIT_LENGTH).endVertex();
            posNormal(bufferBuilder, e.projectVec(new Vec3d(
                    (r1 - 4.0D) / 32.0D,
                    u1 / 32.0D,
                    1.0D
            )), normal).tex(0.0D, (e.getToOffset() + networkOffset) / Measurements.UNIT_LENGTH).endVertex();
        }
    }

    private List<RenderEdge> createRenderEdges(NodeLoop nodeLoop) {
        RenderEdge[] renderEdges = new RenderEdge[nodeLoop.size()];
        for (int i = 0; i < renderEdges.length; i++) {
            renderEdges[i] = RenderEdge.create(nodeLoop.get(i), nodeLoop.get(i + 1));
        }
        return Arrays.asList(renderEdges);
    }

    private void renderTree(AbsoluteTree absoluteTree, double x, double y, double z) {
        List<AbsoluteTree> children = absoluteTree.getChildren();
        for (int i = 0; i < children.size(); i++) {
            AbsoluteTree child = children.get(i);
            Vec3d pos = new Vec3d(absoluteTree.getPos()).scale(0.75D).add(new Vec3d(child.getPos()).scale(0.25D)).addVector(0.5D, 0.5D, 0.5D);

            /*String msg = Integer.toString(i);
            TileEntityRendererDispatcher rendererDispatcher = TileEntityRendererDispatcher.instance;
            float f = rendererDispatcher.entityYaw;
            float f1 = rendererDispatcher.entityPitch;
            EntityRenderer.drawNameplate(Minecraft.getMinecraft().fontRenderer, msg, (float)(pos.x - x), (float)(pos.y - y), (float)(pos.z - z), 0, f, f1, false, false);*/

            renderTree(child, x, y, z);
        }
    }

    public void render(AbsoluteNetworkState network, double x, double y, double z, float partialTicks) {
        NodeLoop nodeLoop = network.getNodeLoop();
        Vec3d viewPos = new Vec3d(x, y, z);

        double networkOffset = network.getOffset() * partialTicks + network.getPreviousOffset() * (1.0F - partialTicks);

        List<RenderEdge> renderEdges = createRenderEdges(nodeLoop);

        renderManager.renderEngine.bindTexture(TEXTURE);
        RenderHelper.enableStandardItemLighting();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();

        GlStateManager.pushMatrix();
        GlStateManager.translate(-x, -y, -z);
        bufferBuilder.begin(GL11.GL_QUADS, VERTEX_FORMAT);
        for (RenderEdge edge : renderEdges) {
            renderEdge(edge, x, y, z, networkOffset, bufferBuilder);
        }
        tessellator.draw();
        GlStateManager.popMatrix();

        for (int i = 0; i < renderEdges.size(); i++) {
            RenderEdge edge = renderEdges.get(i);
            Vec3d pos = edge.projectVec(new Vec3d(0.125D, 0.125D, 0.5D));
            /*TileEntityRendererDispatcher rendererDispatcher = TileEntityRendererDispatcher.instance;
            float f = rendererDispatcher.entityYaw;
            float f1 = rendererDispatcher.entityPitch;
            String msg = Integer.toString(i);
            //String msg = i + ": " + edge.getAngleY();
            EntityRenderer.drawNameplate(Minecraft.getMinecraft().fontRenderer, msg, (float)(pos.x - x), (float)(pos.y - y), (float)(pos.z - z), 0, f, f1, false, false);*/
        }

        renderTree(network.getTree(), x, y, z);

        for (SortedIntShiftMap.Entry<ItemStack> entry : network.getStacks().entries()) {
            double attachmentOffset = (entry.getKey() + networkOffset) % nodeLoop.getLoopLength();
            RenderEdge edge = renderEdges.get(network.getMinNodeIndexForOffset((int)attachmentOffset));
            double d = (attachmentOffset - edge.getFromOffset()) / (edge.getToOffset() - edge.getFromOffset());
            Vec3d pos = edge.projectVec(new Vec3d(2.0D / 16.0D, 0.0D, d));

            GlStateManager.pushMatrix();
            GlStateManager.translate(pos.x - viewPos.x, pos.y - 0.25D - viewPos.y, pos.z - viewPos.z);
            GlStateManager.scale(0.5D, 0.5D, 0.5D);
            GlStateManager.rotate(-edge.getAngleY(), 0.0f, 1.0f, 0.0f);

            renderItem.renderItem(entry.getValue(), ItemCameraTransforms.TransformType.FIXED);

            GlStateManager.popMatrix();

            /*TileEntityRendererDispatcher rendererDispatcher = TileEntityRendererDispatcher.instance;
            float f = rendererDispatcher.entityYaw;
            float f1 = rendererDispatcher.entityPitch;
            String msg = Integer.toString(entry.getValue().getOffset());
            //String msg = i + ": " + edge.getAngleY();
            EntityRenderer.drawNameplate(Minecraft.getMinecraft().fontRenderer, msg, (float)(pos.x - x), (float)(pos.y - y), (float)(pos.z - z), 0, f, f1, false, false);*/
        }
    }
}
