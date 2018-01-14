package com.jamieswhiteshirt.clothesline.client;

import com.jamieswhiteshirt.clothesline.Clothesline;
import com.jamieswhiteshirt.clothesline.api.IConnector;
import com.jamieswhiteshirt.clothesline.api.Measurements;
import com.jamieswhiteshirt.clothesline.api.Network;
import com.jamieswhiteshirt.clothesline.api.ICommonNetworkManager;
import com.jamieswhiteshirt.clothesline.api.util.MutableSortedIntMap;
import com.jamieswhiteshirt.clothesline.client.entity.EntityNetworkRaytraceHit;
import com.jamieswhiteshirt.clothesline.client.network.messagehandler.*;
import com.jamieswhiteshirt.clothesline.client.renderer.LineProjection;
import com.jamieswhiteshirt.clothesline.client.renderer.RenderClotheslineNetwork;
import com.jamieswhiteshirt.clothesline.client.renderer.RenderEdge;
import com.jamieswhiteshirt.clothesline.client.renderer.RenderNetworkState;
import com.jamieswhiteshirt.clothesline.client.renderer.tileentity.TileEntityClotheslineAnchorRenderer;
import com.jamieswhiteshirt.clothesline.common.ClotheslineItems;
import com.jamieswhiteshirt.clothesline.common.CommonProxy;
import com.jamieswhiteshirt.clothesline.common.Util;
import com.jamieswhiteshirt.clothesline.common.impl.CommonNetworkManager;
import com.jamieswhiteshirt.clothesline.common.item.ItemConnector;
import com.jamieswhiteshirt.clothesline.common.network.message.*;
import com.jamieswhiteshirt.clothesline.common.tileentity.TileEntityClotheslineAnchor;
import com.jamieswhiteshirt.clothesline.core.event.GetMouseOverEvent;
import com.jamieswhiteshirt.clothesline.core.event.ClientStoppedUsingItemEvent;
import com.jamieswhiteshirt.clothesline.core.event.RenderEntitiesEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {
    @CapabilityInject(ICommonNetworkManager.class)
    private static final Capability<ICommonNetworkManager> NETWORK_MANAGER_CAPABILITY = Util.nonNullInjected();
    @CapabilityInject(IConnector.class)
    private static final Capability<IConnector> CONNECTOR_CAPABILITY = Util.nonNullInjected();

    private RenderClotheslineNetwork renderClotheslineNetwork;

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);

        Minecraft minecraft = Minecraft.getMinecraft();

        ClientRegistry.bindTileEntitySpecialRenderer(
                TileEntityClotheslineAnchor.class,
                new TileEntityClotheslineAnchorRenderer(minecraft.getRenderItem())
        );
        renderClotheslineNetwork = new RenderClotheslineNetwork(minecraft.getRenderManager(), minecraft.getRenderItem());

        Clothesline.instance.networkWrapper.registerMessage(new MessageSetNetworksHandler(), MessageSetNetworks.class, 0, Side.CLIENT);
        Clothesline.instance.networkWrapper.registerMessage(new MessageAddNetworkHandler(), MessageAddNetwork.class, 1, Side.CLIENT);
        Clothesline.instance.networkWrapper.registerMessage(new MessageRemoveNetworkHandler(), MessageRemoveNetwork.class, 2, Side.CLIENT);
        Clothesline.instance.networkWrapper.registerMessage(new MessageSetAttachmentHandler(), MessageSetAttachment.class, 3, Side.CLIENT);
        Clothesline.instance.networkWrapper.registerMessage(new MessageRemoveAttachmentHandler(), MessageRemoveAttachment.class, 4, Side.CLIENT);
        Clothesline.instance.networkWrapper.registerMessage(new MessageSetNetworkStateHandler(), MessageSetNetworkState.class, 5, Side.CLIENT);
        Clothesline.instance.networkWrapper.registerMessage(new MessageSetConnectorPosHandler(), MessageSetConnectorPos.class, 10, Side.CLIENT);
    }

    @Override
    public CommonNetworkManager createNetworkManager(World world) {
        CommonNetworkManager manager = super.createNetworkManager(world);
        if (world instanceof WorldClient) {
            return manager;
        } else {
            return manager;
        }
    }

    @SubscribeEvent
    public void onClientStoppedUsingItem(ClientStoppedUsingItemEvent event) {
        RayTraceResult objectMouseOver = Minecraft.getMinecraft().objectMouseOver;
        WorldClient world = Minecraft.getMinecraft().world;
        if (objectMouseOver != null && world != null && objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK) {
            EntityPlayerSP player = Minecraft.getMinecraft().player;
            if (player != null && player.getActiveItemStack().getItem() instanceof ItemConnector) {
                // This is a connector item, we must therefore tell the server which block position where the connection
                // will end.
                Clothesline.instance.networkWrapper.sendToServer(new MessageStopUsingItemOn(objectMouseOver.getBlockPos()));

                ItemConnector itemConnector = (ItemConnector) player.getActiveItemStack().getItem();
                itemConnector.stopActiveHandWithToPos(player, objectMouseOver.getBlockPos());

                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void registerModels(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(
                ClotheslineItems.CLOTHESLINE, 0, new ModelResourceLocation(new ResourceLocation("clothesline", "clothesline"), "inventory")
        );
        ModelLoader.setCustomModelResourceLocation(
                ClotheslineItems.CLOTHESLINE_ANCHOR, 0, new ModelResourceLocation(new ResourceLocation("clothesline", "clothesline_anchor"), "inventory")
        );
        ModelLoader.setCustomModelResourceLocation(
                ClotheslineItems.CLOTHESLINE_CRANK, 0, new ModelResourceLocation(new ResourceLocation("clothesline", "clothesline_crank"), "inventory")
        );
        ModelLoader.setCustomModelResourceLocation(
                ClotheslineItems.PULLEY_WHEEL, 0, new ModelResourceLocation(new ResourceLocation("clothesline", "pulley_wheel"), "inventory")
        );
    }

    private void renderHeldClothesline(BlockPos posA, Vec3d vecB, World world, double x, double y, double z) {
        Vec3d vecA = Measurements.midVec(posA);
        BlockPos posB = new BlockPos(vecB);
        int combinedLightA = world.getCombinedLight(posA, 0);
        int combinedLightB = world.getCombinedLight(posB, 0);
        double length = Measurements.UNIT_LENGTH * vecB.distanceTo(vecA);

        renderClotheslineNetwork.buildAndDrawEdgeQuads(bufferBuilder -> {
            renderClotheslineNetwork.renderEdge(0.0D, length, combinedLightA, combinedLightB, LineProjection.create(vecA, vecB), bufferBuilder, x, y, z);
            renderClotheslineNetwork.renderEdge(-length, 0.0D, combinedLightB, combinedLightA, LineProjection.create(vecB, vecA), bufferBuilder, x, y, z);
        });
    }

    private void renderThirdPersonPlayerHeldClothesline(EntityPlayer player, double x, double y, double z, float partialTicks) {
        IConnector connector = player.getCapability(CONNECTOR_CAPABILITY, null);
        if (connector != null) {
            BlockPos posA = connector.getPos();
            if (posA != null) {
                double posX = player.prevPosX + (player.posX - player.prevPosX) * partialTicks;
                double posY = player.prevPosY + (player.posY - player.prevPosY) * partialTicks;
                double posZ = player.prevPosZ + (player.posZ - player.prevPosZ) * partialTicks;

                float yaw = (player.prevRenderYawOffset + (player.renderYawOffset - player.prevRenderYawOffset) * partialTicks) * 0.017453292F;
                int k = player.getPrimaryHand() == EnumHandSide.RIGHT ? 1 : -1;
                double d0 = MathHelper.sin(yaw) * 0.35D;
                double d1 = MathHelper.cos(yaw) * 0.35D;
                Vec3d vecB = new Vec3d(
                        posX - d0 - d1 * k,
                        posY + (player.isSneaking() ? 0.4D : 0.9D),
                        posZ - d0 * k + d1
                );

                renderHeldClothesline(posA, vecB, player.world, x, y, z);
            }
        }
    }

    private void renderFirstPersonPlayerHeldClothesline(EntityPlayer player, double x, double y, double z, float partialTicks) {
        IConnector connector = player.getCapability(CONNECTOR_CAPABILITY, null);
        if (connector != null) {
            BlockPos posA = connector.getPos();
            if (posA != null) {
                double posX = player.prevPosX + (player.posX - player.prevPosX) * partialTicks;
                double posY = player.prevPosY + (player.posY - player.prevPosY) * partialTicks;
                double posZ = player.prevPosZ + (player.posZ - player.prevPosZ) * partialTicks;

                float pitch = (player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * partialTicks) * 0.017453292F;
                float yaw = (player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * partialTicks) * 0.017453292F;
                int k = player.getPrimaryHand() == EnumHandSide.RIGHT ? 1 : -1;
                float f10 = Minecraft.getMinecraft().gameSettings.fovSetting / 100.0F;
                Vec3d vecB = new Vec3d(posX, posY + player.getEyeHeight(), posZ).add(new Vec3d(k * -0.36D * f10, -0.045D * f10, 0.4D).rotatePitch(-pitch).rotateYaw(-yaw));

                renderHeldClothesline(posA, vecB, player.world, x, y, z);
            }
        }
    }

    @SubscribeEvent
    public void onRenderPlayerPost(RenderPlayerEvent.Post event) {
        EntityPlayer player = event.getEntityPlayer();
        float partialTicks = event.getPartialRenderTick();
        double posX = player.prevPosX + (player.posX - player.prevPosX) * partialTicks;
        double posY = player.prevPosY + (player.posY - player.prevPosY) * partialTicks;
        double posZ = player.prevPosZ + (player.posZ - player.prevPosZ) * partialTicks;

        double x = posX + event.getX();
        double y = posY + event.getY();
        double z = posZ + event.getZ();

        renderThirdPersonPlayerHeldClothesline(player, x, y, z, partialTicks);
    }

    @SubscribeEvent
    public void onRenderEntities(RenderEntitiesEvent event) {
        if (MinecraftForgeClient.getRenderPass() == 0) {
            WorldClient world = Minecraft.getMinecraft().world;
            EntityPlayerSP player = Minecraft.getMinecraft().player;
            if (world != null && player != null) {
                float partialTicks = event.getPartialTicks();
                double x = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
                double y = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
                double z = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;

                ICommonNetworkManager manager = world.getCapability(NETWORK_MANAGER_CAPABILITY, null);
                if (manager != null) {
                    boolean showDebugInfo = Minecraft.getMinecraft().gameSettings.showDebugInfo;
                    for (Network network : manager.getNetworks()) {
                        //TODO: Cache the RenderNetworkStates
                        RenderNetworkState renderNetworkState = RenderNetworkState.fromNetworkState(network.getState());
                        renderClotheslineNetwork.render(world, renderNetworkState, x, y, z, partialTicks);
                        if (showDebugInfo) {
                            renderClotheslineNetwork.debugRender(renderNetworkState, x, y, z, partialTicks);
                        }
                    }
                }

                if (Minecraft.getMinecraft().gameSettings.thirdPersonView <= 0) {
                    renderFirstPersonPlayerHeldClothesline(player, x, y, z, partialTicks);
                }
            }
        }
    }

    @SubscribeEvent
    public void onDrawBlockHighlight(DrawBlockHighlightEvent event) {
        RayTraceResult target = event.getTarget();
        if (target.typeOfHit == RayTraceResult.Type.ENTITY) {
            if (target.entityHit instanceof EntityNetworkRaytraceHit) {
                EntityNetworkRaytraceHit entity = (EntityNetworkRaytraceHit) target.entityHit;
                EntityPlayer player = event.getPlayer();
                float partialTicks = event.getPartialTicks();

                double x = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
                double y = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
                double z = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;

                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                GlStateManager.glLineWidth(2.0F);
                GlStateManager.disableTexture2D();
                GlStateManager.depthMask(false);

                entity.getHit().graphHit.renderHighlight(renderClotheslineNetwork, partialTicks, x, y, z, 0.0F, 0.0F, 0.0F, 0.4F);

                GlStateManager.depthMask(true);
                GlStateManager.enableTexture2D();
                GlStateManager.disableBlend();
            }
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END && !Minecraft.getMinecraft().isGamePaused()) {
            WorldClient world = Minecraft.getMinecraft().world;
            if (world != null) {
                ICommonNetworkManager manager = world.getCapability(NETWORK_MANAGER_CAPABILITY, null);
                if (manager != null) {
                    manager.update();
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private static class Ray {
        public final Vec3d from;
        public final Vec3d to;
        public final Vec3d delta;
        public final double lengthSq;

        public Ray(Vec3d from, Vec3d to) {
            this.from = from;
            this.to = to;
            this.delta = to.subtract(from);
            this.lengthSq = delta.dotProduct(delta);
        }

        public Vec3d project(double scalar) {
            return from.add(delta.scale(scalar));
        }
    }

    @SideOnly(Side.CLIENT)
    public abstract static class GraphRaytraceHit {
        public final double distanceSq;

        public GraphRaytraceHit(double distanceSq) {
            this.distanceSq = distanceSq;
        }

        public abstract boolean hitByEntity(ICommonNetworkManager manager, Network network, EntityPlayer player);

        public abstract boolean useItem(ICommonNetworkManager manager, Network network, EntityPlayer player, EnumHand hand);

        public abstract void renderHighlight(RenderClotheslineNetwork renderClotheslineNetwork, float partialTicks, double x, double y, double z, float r, float g, float b, float a);
    }

    @SideOnly(Side.CLIENT)
    public static class EdgeRaytraceHit extends GraphRaytraceHit {
        private final RenderEdge edge;
        private final double offset;

        public EdgeRaytraceHit(double distanceSq, RenderEdge edge, double offset) {
            super(distanceSq);
            this.edge = edge;
            this.offset = offset;
        }

        @Override
        public boolean hitByEntity(ICommonNetworkManager manager, Network network, EntityPlayer player) {
            int offset = (int) Math.round(this.offset);
            int attachmentKey = network.getState().offsetToAttachmentKey(offset);
            Clothesline.instance.networkWrapper.sendToServer(new MessageHitNetwork(network.getUuid(), attachmentKey, offset));
            return true;
        }

        @Override
        public boolean useItem(ICommonNetworkManager manager, Network network, EntityPlayer player, EnumHand hand) {
            int offset = (int) Math.round(this.offset);
            int attachmentKey = network.getState().offsetToAttachmentKey(offset);
            Clothesline.instance.networkWrapper.sendToServer(new MessageTryUseItemOnNetwork(hand, network.getUuid(), attachmentKey));
            return manager.useItem(network, player, hand, attachmentKey);
        }

        @Override
        public void renderHighlight(RenderClotheslineNetwork renderClotheslineNetwork, float partialTicks, double x, double y, double z, float r, float g, float b, float a) {
            renderClotheslineNetwork.renderOutline(edge.getProjection(), x, y, z, r, g, b, a);
        }
    }

    @SideOnly(Side.CLIENT)
    public static class AttachmentRaytraceHit extends GraphRaytraceHit {
        private final int attachmentKey;
        private final AxisAlignedBB attachmentBB;

        public AttachmentRaytraceHit(double distanceSq, int attachmentKey, AxisAlignedBB attachmentBB) {
            super(distanceSq);
            this.attachmentKey = attachmentKey;
            this.attachmentBB = attachmentBB;
        }

        @Override
        public boolean hitByEntity(ICommonNetworkManager manager, Network network, EntityPlayer player) {
            Clothesline.instance.networkWrapper.sendToServer(new MessageHitAttachment(network.getUuid(), attachmentKey));
            manager.hitAttachment(network, player, attachmentKey);
            return true;
        }

        @Override
        public boolean useItem(ICommonNetworkManager manager, Network network, EntityPlayer player, EnumHand hand) {
            return false;
        }

        @Override
        public void renderHighlight(RenderClotheslineNetwork renderClotheslineNetwork, float partialTicks, double x, double y, double z, float r, float g, float b, float a) {
            RenderGlobal.drawSelectionBoundingBox(attachmentBB.offset(-x, -y, -z), r, g, b, a);
        }
    }

    @SideOnly(Side.CLIENT)
    public static final class NetworkRaytraceHit {
        public final GraphRaytraceHit graphHit;
        public final Network network;

        public NetworkRaytraceHit(GraphRaytraceHit graphHit, Network network) {
            this.graphHit = graphHit;
            this.network = network;
        }
    }

    @SubscribeEvent
    public void onGetMouseOver(GetMouseOverEvent event) {
        float partialTicks = event.getPartialTicks();
        Minecraft mc = Minecraft.getMinecraft();
        World world = mc.world;
        Entity renderViewEntity = mc.getRenderViewEntity();
        if (world != null && renderViewEntity != null) {
            ICommonNetworkManager manager = world.getCapability(NETWORK_MANAGER_CAPABILITY, null);
            if (manager != null) {
                Vec3d rayFrom = renderViewEntity.getPositionEyes(partialTicks);
                Vec3d rayTo = mc.objectMouseOver.hitVec;

                Ray ray = new Ray(rayFrom, rayTo);

                NetworkRaytraceHit hit = raytraceNetworks(manager, ray, ray.lengthSq, partialTicks);
                if (hit != null) {
                    EntityNetworkRaytraceHit pointedEntity = new EntityNetworkRaytraceHit(world, manager, hit);
                    mc.objectMouseOver = new RayTraceResult(pointedEntity);
                    mc.pointedEntity = pointedEntity;
                }
            }
        }
    }

    @Nullable
    private NetworkRaytraceHit raytraceNetworks(ICommonNetworkManager manager, Ray ray, double maxDistanceSq, float partialTicks) {
        NetworkRaytraceHit hit = null;
        for (Network network : manager.getNetworks()) {
            //TODO: Cache the RenderNetworkStates
            RenderNetworkState state = RenderNetworkState.fromNetworkState(network.getState());
            GraphRaytraceHit graphHit = raytraceNetwork(state, ray, maxDistanceSq, partialTicks);
            if (graphHit != null && graphHit.distanceSq < maxDistanceSq) {
                maxDistanceSq = graphHit.distanceSq;
                hit = new NetworkRaytraceHit(graphHit, network);
            }
        }
        return hit;
    }

    @Nullable
    private GraphRaytraceHit raytraceNetwork(RenderNetworkState state, Ray ray, double maxDistanceSq, float partialTicks) {
        GraphRaytraceHit hit = null;

        List<RenderEdge> renderEdges = state.getEdges();
        for (RenderEdge edge : renderEdges) {
            GraphRaytraceHit treeHit = raytraceEdge(ray, edge, maxDistanceSq);
            if (treeHit != null) {
                maxDistanceSq = treeHit.distanceSq;
                hit = treeHit;
            }
        }

        double networkOffset = state.getShift(partialTicks);
        AxisAlignedBB basicBB = new AxisAlignedBB(-0.25D, -0.5D, -0.25D, 0.25D, 0.0D, 0.25D);

        for (MutableSortedIntMap.Entry<ItemStack> entry : state.getStacks().entries()) {
            double attachmentOffset = (entry.getKey() + networkOffset) % state.getStacks().getMaxKey();
            int edgeIndex = state.getEdgeIndexForOffset((int)attachmentOffset);
            RenderEdge edge = renderEdges.get(edgeIndex);
            double relativeOffset = attachmentOffset - edge.getFromOffset();
            double edgePosScalar = relativeOffset / (edge.getToOffset() - edge.getFromOffset());
            Vec3d pos = edge.getProjection().projectRUF(-2.0D / 16.0D, 0.0D, edgePosScalar);
            AxisAlignedBB attachmentBB = basicBB.offset(pos);

            RayTraceResult result = attachmentBB.calculateIntercept(ray.from, ray.to);
            if (result != null) {
                double distanceSq = result.hitVec.squareDistanceTo(ray.from);
                if (distanceSq < maxDistanceSq) {
                    maxDistanceSq = distanceSq;
                    hit = new AttachmentRaytraceHit(distanceSq, entry.getKey(), attachmentBB);
                }
            }
        }
        return hit;
    }

    @Nullable
    private EdgeRaytraceHit raytraceEdge(Ray viewRay, RenderEdge edge, double maxDistanceSq) {
        Ray edgeRay = new Ray(edge.getProjection().projectRUF(-2.0D / 16.0D, 0.0D, 0.0D), edge.getProjection().projectRUF(-2.0D / 16.0D, 0.0D, 1.0D));

        double b = viewRay.delta.dotProduct(edgeRay.delta);
        Vec3d w0 = viewRay.from.subtract(edgeRay.from);
        double denominator = viewRay.lengthSq * edgeRay.lengthSq - b * b;
        if (denominator != 0.0D) {
            double d = viewRay.delta.dotProduct(w0);
            double e = edgeRay.delta.dotProduct(w0);
            double viewDeltaScalar = MathHelper.clamp((b * e - edgeRay.lengthSq * d) / denominator, 0.0D, 1.0D);
            double edgeDeltaScalar = MathHelper.clamp((viewRay.lengthSq * e - b * d) / denominator, 0.0D, 1.0D);

            Vec3d viewNear = viewRay.project(viewDeltaScalar);
            Vec3d edgeNear = edgeRay.project(edgeDeltaScalar);

            Vec3d nearDelta = edgeNear.subtract(viewNear);
            if (nearDelta.lengthSquared() < (1.0D / 16.0D) * (1.0D / 16.0D)) {
                double rayLengthSquared = (viewNear.subtract(viewRay.from)).lengthSquared();
                if (rayLengthSquared < maxDistanceSq) {
                    return new EdgeRaytraceHit(rayLengthSquared, edge, edge.getFromOffset() * (1.0D - edgeDeltaScalar) + edge.getToOffset() * edgeDeltaScalar);
                }
            }
        }

        return null;
    }
}
