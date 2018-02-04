package com.jamieswhiteshirt.clothesline.client.renderer;

import com.jamieswhiteshirt.clothesline.Clothesline;
import com.jamieswhiteshirt.clothesline.api.*;
import com.jamieswhiteshirt.clothesline.api.client.IClientNetworkEdge;
import com.jamieswhiteshirt.clothesline.api.client.LineProjection;
import com.jamieswhiteshirt.clothesline.api.util.MutableSortedIntMap;
import com.jamieswhiteshirt.rtree3i.Entry;
import com.jamieswhiteshirt.rtree3i.RTree;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

@SideOnly(Side.CLIENT)
public final class RenderClotheslineNetwork {
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

    public void renderEdge(double fromOffset, double toOffset, int combinedLightFrom, int combinedLightTo, LineProjection p, BufferBuilder bufferBuilder, double x, double y, double z) {
        int lightFrom1 = combinedLightFrom >> 16 & 0xFFFF;
        int lightFrom2 = combinedLightFrom & 0xFFFF;
        int lightTo1 = combinedLightTo >> 16 & 0xFFFF;
        int lightTo2 = combinedLightTo & 0xFFFF;
        double vFrom = fromOffset / Measurements.UNIT_LENGTH;
        double vTo = toOffset / Measurements.UNIT_LENGTH;

        for (int j = 0; j < 4; j++) {
            double r1 = RIGHT_MULTIPLIERS[j];
            double r2 = RIGHT_MULTIPLIERS[j + 1];
            double u1 = UP_MULTIPLIERS[j];
            double u2 = UP_MULTIPLIERS[j + 1];
            double nr = NORMAL_RIGHT_MULTIPLIERS[j];
            double nu = NORMAL_UP_MULTIPLIERS[j];

            Vec3d normal = p.projectTangentRU(nr, nu);
            posNormal(bufferBuilder, p.projectRUF(
                    (r1 - 4.0D) / 32.0D,
                    u1 / 32.0D,
                    0.0D
            ).subtract(x, y, z), normal).tex(0.0D, vFrom).lightmap(lightFrom1, lightFrom2).endVertex();
            posNormal(bufferBuilder, p.projectRUF(
                    (r2 - 4.0D) / 32.0D,
                    u2 / 32.0D,
                    0.0D
            ).subtract(x, y, z), normal).tex(1.0D, vFrom).lightmap(lightFrom1, lightFrom2).endVertex();
            posNormal(bufferBuilder, p.projectRUF(
                    (r2 - 4.0D) / 32.0D,
                    u2 / 32.0D,
                    1.0D
            ).subtract(x, y, z), normal).tex(1.0D, vTo).lightmap(lightTo1, lightTo2).endVertex();
            posNormal(bufferBuilder, p.projectRUF(
                    (r1 - 4.0D) / 32.0D,
                    u1 / 32.0D,
                    1.0D
            ).subtract(x, y, z), normal).tex(0.0D, vTo).lightmap(lightTo1, lightTo2).endVertex();
        }
    }

    private void renderEdge(IBlockAccess world, IClientNetworkEdge e, double x, double y, double z, BufferBuilder bufferBuilder, float partialTicks) {
        Graph.Edge ge = e.getGraphEdge();
        int combinedLightFrom = world.getCombinedLight(ge.getFromKey(), 0);
        int combinedLightTo = world.getCombinedLight(ge.getToKey(), 0);
        double shift = e.getNetwork().getState().getShift(partialTicks);
        renderEdge(ge.getFromOffset() - shift, ge.getToOffset() - shift, combinedLightFrom, combinedLightTo, e.getProjection(), bufferBuilder, x, y, z);
    }

    public void buildAndDrawEdgeQuads(Consumer<BufferBuilder> consumer) {
        renderManager.renderEngine.bindTexture(TEXTURE);
        RenderHelper.enableStandardItemLighting();
        Minecraft.getMinecraft().entityRenderer.enableLightmap();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();

        bufferBuilder.begin(GL11.GL_QUADS, VERTEX_FORMAT);
        consumer.accept(bufferBuilder);
        tessellator.draw();
    }

    public void render(IBlockAccess world, RTree<IClientNetworkEdge> edgesTree, ICamera camera, double x, double y, double z, float partialTicks) {
        Vec3d viewPos = new Vec3d(x, y, z);

        Collection<Entry<IClientNetworkEdge>> entries = edgesTree.search(box -> camera.isBoundingBoxInFrustum(new AxisAlignedBB(box.x1(), box.y1(), box.z1(), box.x2(), box.y2(), box.z2())));

        buildAndDrawEdgeQuads(bufferBuilder -> {
            for (Entry<IClientNetworkEdge> entry : entries) {
                renderEdge(world, entry.getValue(), x, y, z, bufferBuilder, partialTicks);
            }
        });

        GlStateManager.enableRescaleNormal();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        GlStateManager.enableBlend();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

        for (Entry<IClientNetworkEdge> edgeEntry : entries) {
            IClientNetworkEdge edge = edgeEntry.getValue();
            Graph.Edge graphEdge = edge.getGraphEdge();
            AbsoluteNetworkState state = edge.getNetwork().getState();
            double fromAttachmentKey = state.offsetToAttachmentKey(graphEdge.getFromOffset(), partialTicks);
            double toAttachmentKey = state.offsetToAttachmentKey(graphEdge.getToOffset(), partialTicks);

            List<MutableSortedIntMap.Entry<ItemStack>> attachments = state.getAttachmentsInRange((int) fromAttachmentKey, (int) toAttachmentKey);
            if (!attachments.isEmpty()) {
                float angleY = graphEdge.getKey().getAngle();
                Graph.Edge previousGraphEdge = state.getGraph().getEdgeForOffset(graphEdge.getFromOffset() - 1);
                float angleDiff = (graphEdge.getKey().getAngle() - previousGraphEdge.getKey().getAngle() + 360.0F) % 360.0F;
                float speedRatio = (float) state.getMomentum(partialTicks) / AbsoluteNetworkState.MAX_MOMENTUM;
                float swingMax = angleDiff / 4.0F * speedRatio * speedRatio;

                for (MutableSortedIntMap.Entry<ItemStack> attachmentEntry : attachments) {
                    double attachmentOffset = state.attachmentKeyToOffset(attachmentEntry.getKey(), partialTicks);
                    double relativeOffset = attachmentOffset - graphEdge.getFromOffset();
                    double edgePosScalar = relativeOffset / graphEdge.getLength();
                    Vec3d pos = edge.getProjection().projectRUF(-2.0D / 16.0D, 0.0D, edgePosScalar);

                    int light = world.getCombinedLight(new BlockPos(pos), 0);
                    OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)(light & 0xFFFF), (float)((light >> 16) & 0xFFFF));

                    GlStateManager.pushMatrix();
                    GlStateManager.translate(pos.x - viewPos.x, pos.y - viewPos.y, pos.z - viewPos.z);
                    GlStateManager.scale(0.5D, 0.5D, 0.5D);
                    GlStateManager.rotate(-angleY, 0.0f, 1.0f, 0.0f);
                    GlStateManager.rotate(
                        swingMax *
                            (float)(Math.exp(-relativeOffset / (Measurements.UNIT_LENGTH * 2.0D))) *
                            MathHelper.sin((float)(relativeOffset / (AbsoluteNetworkState.MAX_MOMENTUM * 2.0D)))
                        , 1.0F, 0.0F, 0.0F);
                    GlStateManager.translate(0.0f, -0.5f, 0.0f);

                    renderItem.renderItem(attachmentEntry.getValue(), ItemCameraTransforms.TransformType.FIXED);

                    GlStateManager.popMatrix();
                }
            }
        }

        Minecraft.getMinecraft().entityRenderer.disableLightmap();
    }

    public void renderOutline(LineProjection p, double x, double y, double z, float r, float g, float b, float a) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);

        for (int i = 0; i < 4; i++) {
            double up = (UP_MULTIPLIERS[i] * 1.01D) / 32.0D;
            double right = (RIGHT_MULTIPLIERS[i] * 1.01D - 4.0D) / 32.0D;

            pos(bufferBuilder, p.projectRUF(right, up, 0.0D).subtract(x, y, z)).color(r, g, b, a).endVertex();
            pos(bufferBuilder, p.projectRUF(right, up, 1.0D).subtract(x, y, z)).color(r, g, b, a).endVertex();
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

    /* public void debugRender(RenderNetworkState state, double x, double y, double z, float partialTicks) {
        TileEntityRendererDispatcher rendererDispatcher = TileEntityRendererDispatcher.instance;
        float yaw = rendererDispatcher.entityYaw;
        float pitch = rendererDispatcher.entityPitch;
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;

        List<RenderEdge> renderEdges = state.getEdges();
        for (int i = 0; i < renderEdges.size(); i++) {
            RenderEdge edge = renderEdges.get(i);
            Vec3d pos = edge.getProjection().projectRUF(0.125D, 0.125D, 0.5D);
            debugRenderText(i + ": " + edge.getAngleY(), pos.x - x, pos.y - y, pos.z - z, yaw, pitch, fontRenderer);
        }

        debugRenderTree(state.getTree(), x, y, z, yaw, pitch, fontRenderer);

        double shift = state.getShift(partialTicks);
        for (MutableSortedIntMap.Entry<ItemStack> entry : state.getStacks().entries()) {
            double attachmentOffset = (entry.getKey() + shift) % state.getStacks().getMaxKey();
            int edgeIndex = state.getEdgeIndexForOffset((int)attachmentOffset);
            RenderEdge edge = renderEdges.get(edgeIndex);
            double relativeOffset = attachmentOffset - edge.getFromOffset();
            double edgePosScalar = relativeOffset / (edge.getToOffset() - edge.getFromOffset());
            Vec3d pos = edge.getProjection().projectRUF(-2.0D / 16.0D, 0.0D, edgePosScalar);
            debugRenderText(Integer.toString(entry.getKey()), pos.x - x, pos.y - y, pos.z - z, yaw, pitch, fontRenderer);
        }
    } */
}
