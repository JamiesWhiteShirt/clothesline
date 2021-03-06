package com.jamieswhiteshirt.clothesline.client;

import com.jamieswhiteshirt.clothesline.Clothesline;
import com.jamieswhiteshirt.clothesline.api.*;
import com.jamieswhiteshirt.clothesline.api.util.MutableSortedIntMap;
import com.jamieswhiteshirt.clothesline.client.capability.ClientCapabilityProvider;
import com.jamieswhiteshirt.clothesline.client.raytrace.EntityNetworkRaytraceHit;
import com.jamieswhiteshirt.clothesline.client.impl.ClientNetworkManager;
import com.jamieswhiteshirt.clothesline.client.network.messagehandler.*;
import com.jamieswhiteshirt.clothesline.client.raytrace.AttachmentRaytraceHit;
import com.jamieswhiteshirt.clothesline.client.raytrace.EdgeRaytraceHit;
import com.jamieswhiteshirt.clothesline.client.raytrace.NetworkRaytraceHit;
import com.jamieswhiteshirt.clothesline.client.renderer.RenderClotheslineNetwork;
import com.jamieswhiteshirt.clothesline.client.renderer.tileentity.TileEntityClotheslineAnchorRenderer;
import com.jamieswhiteshirt.clothesline.common.ClotheslineBlocks;
import com.jamieswhiteshirt.clothesline.common.ClotheslineItems;
import com.jamieswhiteshirt.clothesline.common.CommonProxy;
import com.jamieswhiteshirt.clothesline.common.block.BlockClotheslineAnchor;
import com.jamieswhiteshirt.clothesline.common.impl.NetworkCollection;
import com.jamieswhiteshirt.clothesline.common.item.ItemConnector;
import com.jamieswhiteshirt.clothesline.common.network.message.*;
import com.jamieswhiteshirt.clothesline.common.tileentity.TileEntityClotheslineAnchor;
import com.jamieswhiteshirt.clothesline.hooks.api.ClientStoppedUsingItemEvent;
import com.jamieswhiteshirt.clothesline.hooks.api.GetMouseOverEvent;
import com.jamieswhiteshirt.clothesline.hooks.api.RenderEntitiesEvent;
import com.jamieswhiteshirt.clothesline.hooks.api.UseItemMovementEvent;
import com.jamieswhiteshirt.clothesline.internal.IConnector;
import com.jamieswhiteshirt.clothesline.internal.IWorldEventDispatcher;
import com.jamieswhiteshirt.rtree3i.Box;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {
    private static final AxisAlignedBB attachmentBox = new AxisAlignedBB(-0.5D, -0.5D, -0.5D, 0.5D, 0.5D, 0.5D);
    private static final ResourceLocation SOUND_KEY = new ResourceLocation(Clothesline.MODID, "sound");
    private static final ResourceLocation ICONS = new ResourceLocation(Clothesline.MODID, "textures/gui/icons.png");
    private static final int ICONS_WIDTH = 32, ICONS_HEIGHT = 16;

    private RenderClotheslineNetwork renderClotheslineNetwork;

    @Override
    public SimpleNetworkWrapper createNetworkChannel() {
        SimpleNetworkWrapper networkChannel = super.createNetworkChannel();
        networkChannel.registerMessage(new AddNetworkMessageHandler(), AddNetworkMessage.class, 1, Side.CLIENT);
        networkChannel.registerMessage(new RemoveNetworkMessageHandler(), RemoveNetworkMessage.class, 2, Side.CLIENT);
        networkChannel.registerMessage(new SetAttachmentMessageHandler(), SetAttachmentMessage.class, 3, Side.CLIENT);
        networkChannel.registerMessage(new RemoveAttachmentMessageHandler(), RemoveAttachmentMessage.class, 4, Side.CLIENT);
        networkChannel.registerMessage(new SetConnectorPosMessageHandler(), SetConnectorPosMessage.class, 10, Side.CLIENT);
        networkChannel.registerMessage(new UpdateNetworkMessageHandler(), UpdateNetworkMessage.class, 11, Side.CLIENT);
        networkChannel.registerMessage(new SetAnchorHasCrankMessageHandler(), SetAnchorHasCrankMessage.class, 12, Side.CLIENT);
        return networkChannel;
    }

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
    }

    @SubscribeEvent
    public void attachWorldCapabilities(AttachCapabilitiesEvent<World> event) {
        World world = event.getObject();
        if (world instanceof WorldClient) {
            ClientNetworkManager manager = new ClientNetworkManager((WorldClient) world, new NetworkCollection());
            MinecraftForge.EVENT_BUS.post(new NetworkManagerCreatedEvent(world, manager));
            event.addCapability(new ResourceLocation(Clothesline.MODID, "networks"), new ClientCapabilityProvider(manager));
        }
    }

    @SubscribeEvent
    public void onNetworkManagerCreated(NetworkManagerCreatedEvent event) {
        if (event.getWorld().isRemote) {
            event.getNetworkManager().getNetworks().addEventListener(SOUND_KEY, new SoundNetworkCollectionListener());
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
                Clothesline.instance.networkChannel.sendToServer(new StopUsingItemOnMessage(objectMouseOver.getBlockPos()));

                ItemConnector itemConnector = (ItemConnector) player.getActiveItemStack().getItem();
                itemConnector.stopActiveHandWithToPos(player, objectMouseOver.getBlockPos());

                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onUseItemMovement(UseItemMovementEvent event) {
        if (event.getItemStack().getItem() == ClotheslineItems.CLOTHESLINE) {
            event.setMovementSlowed(false);
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
            ClotheslineItems.CLOTHESLINE_ANCHOR, 1, new ModelResourceLocation(new ResourceLocation("clothesline", "pulley_wheel"), "inventory")
        );
        ModelLoader.setCustomModelResourceLocation(
            ClotheslineItems.CLOTHESLINE_ANCHOR, 2, new ModelResourceLocation(new ResourceLocation("clothesline", "pulley_wheel_rope"), "inventory")
        );
        ModelLoader.setCustomModelResourceLocation(
            ClotheslineItems.CRANK, 0, new ModelResourceLocation(new ResourceLocation("clothesline", "crank"), "inventory")
        );
        ModelLoader.setCustomModelResourceLocation(
            ClotheslineItems.SPINNER, 0, new ModelResourceLocation(new ResourceLocation("clothesline", "spinner"), "inventory")
        );
    }

    private void renderHeldClothesline(BlockPos posA, Vec3d vecB, World world, double x, double y, double z) {
        Vec3d vecA = Utility.midVec(posA);
        BlockPos posB = new BlockPos(vecB);
        int combinedLightA = world.getCombinedLight(posA, 0);
        int combinedLightB = world.getCombinedLight(posB, 0);
        double length = AttachmentUnit.UNITS_PER_BLOCK * vecB.distanceTo(vecA);

        renderClotheslineNetwork.buildAndDrawEdgeQuads(bufferBuilder -> {
            renderClotheslineNetwork.renderEdge(0.0D, length, combinedLightA, combinedLightB, LineProjection.create(vecA, vecB), bufferBuilder, x, y, z);
            renderClotheslineNetwork.renderEdge(-length, 0.0D, combinedLightB, combinedLightA, LineProjection.create(vecB, vecA), bufferBuilder, x, y, z);
        });
    }

    private void renderThirdPersonPlayerHeldClothesline(EntityPlayer player, double x, double y, double z, float partialTicks) {
        IConnector connector = player.getCapability(Clothesline.CONNECTOR_CAPABILITY, null);
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
        IConnector connector = player.getCapability(Clothesline.CONNECTOR_CAPABILITY, null);
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
            Entity renderViewEntity = Minecraft.getMinecraft().getRenderViewEntity();
            if (world != null && renderViewEntity != null) {
                float partialTicks = event.getPartialTicks();
                double x = renderViewEntity.lastTickPosX + (renderViewEntity.posX - renderViewEntity.lastTickPosX) * partialTicks;
                double y = renderViewEntity.lastTickPosY + (renderViewEntity.posY - renderViewEntity.lastTickPosY) * partialTicks;
                double z = renderViewEntity.lastTickPosZ + (renderViewEntity.posZ - renderViewEntity.lastTickPosZ) * partialTicks;

                INetworkManager manager = world.getCapability(Clothesline.NETWORK_MANAGER_CAPABILITY, null);
                if (manager != null) {
                    boolean showDebugInfo = Minecraft.getMinecraft().gameSettings.showDebugInfo;
                    world.profiler.startSection("renderClotheslines");
                    renderClotheslineNetwork.render(world, manager.getNetworks().getEdges(), event.getCamera(), x, y, z, partialTicks);
                    world.profiler.endSection();
                    if (showDebugInfo) {
                        renderClotheslineNetwork.debugRender(manager.getNetworks().getNodes(), manager.getNetworks().getEdges(), event.getCamera(), x, y, z, event.getPartialTicks());
                    }
                }

                if (Minecraft.getMinecraft().gameSettings.thirdPersonView <= 0 && renderViewEntity instanceof EntityPlayer) {
                    renderFirstPersonPlayerHeldClothesline((EntityPlayer) renderViewEntity, x, y, z, partialTicks);
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
                Entity renderViewEntity = Minecraft.getMinecraft().getRenderViewEntity();
                float partialTicks = event.getPartialTicks();

                double x = renderViewEntity.lastTickPosX + (renderViewEntity.posX - renderViewEntity.lastTickPosX) * partialTicks;
                double y = renderViewEntity.lastTickPosY + (renderViewEntity.posY - renderViewEntity.lastTickPosY) * partialTicks;
                double z = renderViewEntity.lastTickPosZ + (renderViewEntity.posZ - renderViewEntity.lastTickPosZ) * partialTicks;

                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                GlStateManager.glLineWidth(2.0F);
                GlStateManager.disableTexture2D();
                GlStateManager.depthMask(false);

                entity.getHit().renderHighlight(renderClotheslineNetwork, partialTicks, x, y, z, 0.0F, 0.0F, 0.0F, 0.4F);

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
                IWorldEventDispatcher eventDispatcher = world.getCapability(Clothesline.WORLD_EVENT_DISPATCHER_CAPABILITY, null);
                if (eventDispatcher != null) {
                    eventDispatcher.onTick();
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

    @SubscribeEvent
    public void onGetMouseOver(GetMouseOverEvent event) {
        float partialTicks = event.getPartialTicks();
        Minecraft mc = Minecraft.getMinecraft();
        World world = mc.world;
        Entity renderViewEntity = mc.getRenderViewEntity();
        RayTraceResult objectMouseOver = mc.objectMouseOver;
        if (world != null && renderViewEntity != null && objectMouseOver != null) {
            INetworkManager manager = world.getCapability(Clothesline.NETWORK_MANAGER_CAPABILITY, null);
            if (manager != null) {
                Vec3d rayFrom = renderViewEntity.getPositionEyes(partialTicks);
                Vec3d rayTo = objectMouseOver.hitVec;

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
    private NetworkRaytraceHit raytraceNetworks(INetworkManager manager, Ray ray, double maxDistanceSq, float partialTicks) {
        Box box = Box.create(
            (int) Math.floor(Math.min(ray.from.x, ray.to.x) - 0.5D),
            (int) Math.floor(Math.min(ray.from.y, ray.to.y) - 0.5D),
            (int) Math.floor(Math.min(ray.from.z, ray.to.z) - 0.5D),
            (int) Math.ceil(Math.max(ray.from.x, ray.to.x) + 0.5D),
            (int) Math.ceil(Math.max(ray.from.y, ray.to.y) + 0.5D),
            (int) Math.ceil(Math.max(ray.from.z, ray.to.z) + 0.5D)
        );

        NetworkRaytraceHit hit = null;
        List<INetworkEdge> edges = manager.getNetworks().getEdges().values(box::intersectsClosed).collect(Collectors.toList());
        for (INetworkEdge edge : edges) {
            NetworkRaytraceHit hitCandidate = raytraceEdge(ray, edge, maxDistanceSq, partialTicks);
            if (hitCandidate != null && hitCandidate.distanceSq < maxDistanceSq) {
                maxDistanceSq = hitCandidate.distanceSq;
                hit = hitCandidate;
            }
        }

        return hit;
    }

    @Nullable
    private NetworkRaytraceHit raytraceEdge(Ray viewRay, INetworkEdge edge, double maxDistanceSq, float partialTicks) {
        Path.Edge pathEdge = edge.getPathEdge();
        LineProjection projection = LineProjection.create(edge);
        NetworkRaytraceHit hit = null;

        Ray edgeRay = new Ray(projection.projectRUF(-2.0D / 16.0D, 0.0D, 0.0D), projection.projectRUF(-2.0D / 16.0D, 0.0D, 1.0D));

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
                    double offset = pathEdge.getFromOffset() * (1.0D - edgeDeltaScalar) + pathEdge.getToOffset() * edgeDeltaScalar;
                    hit = new EdgeRaytraceHit(rayLengthSquared, edge, offset);
                }
            }
        }

        INetworkState state = edge.getNetwork().getState();
        double fromAttachmentKey = state.offsetToAttachmentKey(pathEdge.getFromOffset(), partialTicks);
        double toAttachmentKey = state.offsetToAttachmentKey(pathEdge.getToOffset(), partialTicks);
        List<MutableSortedIntMap.Entry<ItemStack>> attachments = state.getAttachmentsInRange((int) fromAttachmentKey, (int) toAttachmentKey);
        if (!attachments.isEmpty()) {
            Vector4f lFrom = new Vector4f();
            Vector4f lTo = new Vector4f();
            Vector4f wHitVec = new Vector4f();

            EdgeAttachmentProjector projector = EdgeAttachmentProjector.build(edge);
            for (MutableSortedIntMap.Entry<ItemStack> attachment : attachments) {
                double attachmentOffset = state.attachmentKeyToOffset(attachment.getKey(), partialTicks);
                // Local space to world space matrix
                Matrix4f l2w = projector.getL2WForAttachment(state.getMomentum(partialTicks), attachmentOffset, partialTicks);

                // World space to local space matrix
                Matrix4f w2l = new Matrix4f();
                Matrix4f.invert(l2w, w2l);

                Matrix4f.transform(w2l, new Vector4f((float) viewRay.from.x, (float) viewRay.from.y, (float) viewRay.from.z, 1.0F), lFrom);
                Matrix4f.transform(w2l, new Vector4f((float) viewRay.to.x, (float) viewRay.to.y, (float) viewRay.to.z, 1.0F), lTo);

                RayTraceResult result = attachmentBox.calculateIntercept(new Vec3d(lFrom.x, lFrom.y, lFrom.z), new Vec3d(lTo.x, lTo.y, lTo.z));
                if (result != null) {
                    Matrix4f.transform(l2w, new Vector4f((float) result.hitVec.x, (float) result.hitVec.y, (float) result.hitVec.z, 1.0F), wHitVec);
                    double distanceSq = new Vec3d(wHitVec.x, wHitVec.y, wHitVec.z).squareDistanceTo(viewRay.from);
                    if (distanceSq < maxDistanceSq) {
                        maxDistanceSq = distanceSq;
                        hit = new AttachmentRaytraceHit(distanceSq, edge, attachment.getKey(), l2w);
                    }
                }
            }
        }

        return hit;
    }

    @SubscribeEvent
    public void onPostRenderCrosshair(RenderGameOverlayEvent.Post event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS) {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc.gameSettings.thirdPersonView == 0) {
                RayTraceResult objectMouseOver = mc.objectMouseOver;
                if (objectMouseOver != null && objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK) {
                    BlockPos pos = objectMouseOver.getBlockPos();
                    if (mc.world.getBlockState(pos).getBlock() == ClotheslineBlocks.CLOTHESLINE_ANCHOR) {
                        TileEntityClotheslineAnchor tileEntity = BlockClotheslineAnchor.getTileEntity(mc.world, pos);
                        if (tileEntity != null && tileEntity.getHasCrank()) {
                            Vec3d hitVec = objectMouseOver.hitVec;
                            ScaledResolution scaledResolution = event.getResolution();

                            int scaledWidth = scaledResolution.getScaledWidth();
                            int scaledHeight = scaledResolution.getScaledHeight();
                            int offset = BlockClotheslineAnchor.getCrankMultiplier(pos, hitVec.x, hitVec.z, mc.player) > 0 ? 0 : 16;

                            mc.getTextureManager().bindTexture(ICONS);
                            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                            GlStateManager.enableAlpha();
                            Gui.drawModalRectWithCustomSizedTexture(scaledWidth / 2 - 15 + offset, scaledHeight / 2 - 7, offset, 0, 16, 16, ICONS_WIDTH, ICONS_HEIGHT);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onRenderTextOverlay(RenderGameOverlayEvent.Text event) {
        Minecraft mc = Minecraft.getMinecraft();
        RayTraceResult objectMouseOver = mc.objectMouseOver;
        if (mc.gameSettings.showDebugInfo && objectMouseOver != null) {
            if (objectMouseOver.typeOfHit == RayTraceResult.Type.ENTITY) {
                Entity entity = objectMouseOver.entityHit;
                if (entity instanceof EntityNetworkRaytraceHit) {
                    NetworkRaytraceHit hit = ((EntityNetworkRaytraceHit) entity).getHit();
                    INetworkEdge edge = hit.edge;
                    INetwork network = edge.getNetwork();
                    event.getRight().addAll(Arrays.asList(
                        "",
                        "Network ID: " + network.getId(),
                        "Path index: " + edge.getIndex(),
                        "Span: " + edge.getPathEdge().getFromOffset() + " to " + edge.getPathEdge().getToOffset()
                    ));

                    if (hit instanceof EdgeRaytraceHit) {
                        event.getRight().add("Position: " + Math.round(((EdgeRaytraceHit)hit).offset));
                    }
                    if (hit instanceof AttachmentRaytraceHit) {
                        event.getRight().add("Attachment: " + ((AttachmentRaytraceHit)hit).attachmentKey);
                    }
                }
            } else if (objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK) {
                INetworkManager manager = mc.world.getCapability(Clothesline.NETWORK_MANAGER_CAPABILITY, null);
                if (manager != null) {
                    INetworkNode node = manager.getNetworks().getNodes().get(objectMouseOver.getBlockPos());
                    if (node != null) {
                        event.getRight().addAll(Arrays.asList(
                            "",
                            "Network ID: " + node.getNetwork().getId()
                        ));
                    }
                }
            }
        }
    }
}
