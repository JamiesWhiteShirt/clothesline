package com.jamieswhiteshirt.clothesline.client.renderer;

import com.jamieswhiteshirt.clothesline.Clothesline;
import com.jamieswhiteshirt.clothesline.api.*;
import com.jamieswhiteshirt.clothesline.api.client.EdgeAttachmentProjector;
import com.jamieswhiteshirt.clothesline.api.client.IClientNetworkEdge;
import com.jamieswhiteshirt.clothesline.api.client.LineProjection;
import com.jamieswhiteshirt.clothesline.api.util.MutableSortedIntMap;
import com.jamieswhiteshirt.rtree3i.RTreeMap;
import com.jamieswhiteshirt.rtree3i.Selection;
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
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

import java.nio.FloatBuffer;
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
        double vFrom = fromOffset / AttachmentUnit.UNITS_PER_BLOCK;
        double vTo = toOffset / AttachmentUnit.UNITS_PER_BLOCK;

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

    private void renderEdge(IBlockAccess world, IClientNetworkEdge edge, double x, double y, double z, BufferBuilder bufferBuilder, float partialTicks) {
        Path.Edge ge = edge.getPathEdge();
        Line line = ge.getLine();
        int combinedLightFrom = world.getCombinedLight(line.getFromPos(), 0);
        int combinedLightTo = world.getCombinedLight(line.getToPos(), 0);
        double shift = edge.getNetwork().getState().getShift(partialTicks);
        renderEdge(ge.getFromOffset() - shift, ge.getToOffset() - shift, combinedLightFrom, combinedLightTo, edge.getProjection(), bufferBuilder, x, y, z);
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

    public void render(IBlockAccess world, RTreeMap<Line, IClientNetworkEdge> edgesMap, ICamera camera, double x, double y, double z, float partialTicks) {
        Vec3d viewPos = new Vec3d(x, y, z);

        // Select all entries in the edge map intersecting with the camera frustum
        Selection<IClientNetworkEdge> edges = edgesMap
            .values(box -> camera.isBoundingBoxInFrustum(new AxisAlignedBB(box.x1(), box.y1(), box.z1(), box.x2(), box.y2(), box.z2())));

        // Draw the rope for all edges
        buildAndDrawEdgeQuads(bufferBuilder -> edges.forEach(edge -> renderEdge(world, edge, x, y, z, bufferBuilder, partialTicks)));

        GlStateManager.enableRescaleNormal();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        GlStateManager.enableBlend();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

        // Local position of attachment item
        Vector4f lPos = new Vector4f(0.0F, 0.0F, 0.0F, 1.0F);
        // World position of attachment item
        Vector4f wPos = new Vector4f();
        // Buffer for local space to world space matrix to upload to GL
        FloatBuffer l2wBuffer = GLAllocation.createDirectFloatBuffer(16);

        edges.forEach(edge -> {
            Path.Edge pathEdge = edge.getPathEdge();
            INetworkState state = edge.getNetwork().getState();
            double fromAttachmentKey = state.offsetToAttachmentKey(pathEdge.getFromOffset(), partialTicks);
            double toAttachmentKey = state.offsetToAttachmentKey(pathEdge.getToOffset(), partialTicks);

            List<MutableSortedIntMap.Entry<ItemStack>> attachments = state.getAttachmentsInRange((int) fromAttachmentKey, (int) toAttachmentKey);
            if (!attachments.isEmpty()) {
                EdgeAttachmentProjector projector = edge.getProjector();

                for (MutableSortedIntMap.Entry<ItemStack> attachmentEntry : attachments) {
                    double attachmentOffset = state.attachmentKeyToOffset(attachmentEntry.getKey(), partialTicks);
                    // Local space to world space matrix
                    Matrix4f l2w = projector.getL2WForAttachment(state.getMomentum(partialTicks), attachmentOffset, partialTicks);

                    // Create world position of attachment for lighting calculation
                    Matrix4f.transform(l2w, lPos, wPos);
                    int light = world.getCombinedLight(new BlockPos(MathHelper.floor(wPos.x), MathHelper.floor(wPos.y), MathHelper.floor(wPos.z)), 0);
                    OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)(light & 0xFFFF), (float)((light >> 16) & 0xFFFF));

                    // Store and flip to be ready for read
                    l2w.store(l2wBuffer);
                    l2wBuffer.flip();

                    GlStateManager.pushMatrix();
                    GlStateManager.translate(-viewPos.x, -viewPos.y, -viewPos.z);
                    GlStateManager.multMatrix(l2wBuffer);
                    renderItem.renderItem(attachmentEntry.getValue(), ItemCameraTransforms.TransformType.FIXED);
                    GlStateManager.popMatrix();

                    l2wBuffer.clear();
                }
            }
        });

        GlStateManager.disableRescaleNormal();
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

    public void debugRender(
        RTreeMap<BlockPos, INetworkNode> nodesMap,
        RTreeMap<Line, IClientNetworkEdge> edgesMap,
        ICamera camera, double x, double y, double z, float partialTicks
    ) {
        TileEntityRendererDispatcher rendererDispatcher = TileEntityRendererDispatcher.instance;
        float yaw = rendererDispatcher.entityYaw;
        float pitch = rendererDispatcher.entityPitch;
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;

        // Select all edges in the edges map intersecting with the camera frustum
        Selection<IClientNetworkEdge> edges = edgesMap
            .values(box -> camera.isBoundingBoxInFrustum(new AxisAlignedBB(box.x1(), box.y1(), box.z1(), box.x2(), box.y2(), box.z2())));

        edges.forEach(edge -> {
            Path.Edge pathEdge = edge.getPathEdge();
            BlockPos nodePos = pathEdge.getLine().getFromPos();
            INetworkNode node = nodesMap.get(nodePos);
            Path.Node pathNode = node.getPathNode();
            int nodeIndex = pathNode.getEdges().indexOf(pathEdge);
            int networkIndex = edge.getNetwork().getState().getPath().getEdges().indexOf(pathEdge);
            Vec3d pos = edge.getProjection().projectRUF(-0.125D, 0.125D, 0.5D);
            debugRenderText(nodeIndex + " " + networkIndex, pos.x - x, pos.y - y, pos.z - z, yaw, pitch, fontRenderer);
        });
    }
}
