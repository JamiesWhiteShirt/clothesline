package com.jamieswhiteshirt.clothesline.client;

import com.jamieswhiteshirt.clothesline.Clothesline;
import com.jamieswhiteshirt.clothesline.api.*;
import com.jamieswhiteshirt.clothesline.api.client.IClientNetworkEdge;
import com.jamieswhiteshirt.clothesline.api.client.IClientNetworkManager;
import com.jamieswhiteshirt.clothesline.api.util.MutableSortedIntMap;
import com.jamieswhiteshirt.clothesline.client.capability.ClientNetworkManagerProvider;
import com.jamieswhiteshirt.clothesline.client.raytrace.EntityNetworkRaytraceHit;
import com.jamieswhiteshirt.clothesline.client.impl.ClientNetworkManager;
import com.jamieswhiteshirt.clothesline.client.network.messagehandler.*;
import com.jamieswhiteshirt.clothesline.api.client.LineProjection;
import com.jamieswhiteshirt.clothesline.client.raytrace.AttachmentRaytraceHit;
import com.jamieswhiteshirt.clothesline.client.raytrace.EdgeRaytraceHit;
import com.jamieswhiteshirt.clothesline.client.raytrace.NetworkRaytraceHit;
import com.jamieswhiteshirt.clothesline.client.renderer.EdgeAttachmentProjector;
import com.jamieswhiteshirt.clothesline.client.renderer.RenderClotheslineNetwork;
import com.jamieswhiteshirt.clothesline.client.renderer.tileentity.TileEntityClotheslineAnchorRenderer;
import com.jamieswhiteshirt.clothesline.common.ClotheslineItems;
import com.jamieswhiteshirt.clothesline.common.CommonProxy;
import com.jamieswhiteshirt.clothesline.common.item.ItemConnector;
import com.jamieswhiteshirt.clothesline.common.network.message.*;
import com.jamieswhiteshirt.clothesline.common.tileentity.TileEntityClotheslineAnchor;
import com.jamieswhiteshirt.clothesline.hooks.api.ClientStoppedUsingItemEvent;
import com.jamieswhiteshirt.clothesline.hooks.api.GetMouseOverEvent;
import com.jamieswhiteshirt.clothesline.hooks.api.RenderEntitiesEvent;
import com.jamieswhiteshirt.clothesline.hooks.api.UseItemMovementEvent;
import com.jamieswhiteshirt.rtree3i.Box;
import com.jamieswhiteshirt.rtree3i.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
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
import java.util.List;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {
    private static final AxisAlignedBB attachmentBox = new AxisAlignedBB(-0.5D, -0.5D, -0.5D, 0.5D, 0.5D, 0.5D);

    private RenderClotheslineNetwork renderClotheslineNetwork;

    @Override
    public SimpleNetworkWrapper createNetworkChannel() {
        SimpleNetworkWrapper networkChannel = super.createNetworkChannel();
        networkChannel.registerMessage(new SetNetworksMessageHandler(), SetNetworkMessage.class, 0, Side.CLIENT);
        networkChannel.registerMessage(new AddNetworkMessageHandler(), AddNetworkMessage.class, 1, Side.CLIENT);
        networkChannel.registerMessage(new RemoveNetworkMessageHandler(), RemoveNetworkMessage.class, 2, Side.CLIENT);
        networkChannel.registerMessage(new SetAttachmentMessageHandler(), SetAttachmentMessage.class, 3, Side.CLIENT);
        networkChannel.registerMessage(new RemoveAttachmentMessageHandler(), RemoveAttachmentMessage.class, 4, Side.CLIENT);
        networkChannel.registerMessage(new SetNetworkStateMessageHandler(), SetNetworkStateMessage.class, 5, Side.CLIENT);
        networkChannel.registerMessage(new SetConnectorPosMessageHandler(), SetConnectorPosMessage.class, 10, Side.CLIENT);
        networkChannel.registerMessage(new UpdateNetworkMessageHandler(), UpdateNetworkMessage.class, 11, Side.CLIENT);
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
            ClientNetworkManager manager = new ClientNetworkManager((WorldClient) world);
            event.addCapability(new ResourceLocation(Clothesline.MODID, "network_manager"), new ClientNetworkManagerProvider(manager));
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
            EntityPlayerSP player = Minecraft.getMinecraft().player;
            if (world != null && player != null) {
                float partialTicks = event.getPartialTicks();
                double x = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
                double y = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
                double z = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;

                IClientNetworkManager manager = world.getCapability(Clothesline.CLIENT_NETWORK_MANAGER_CAPABILITY, null);
                if (manager != null) {
                    boolean showDebugInfo = Minecraft.getMinecraft().gameSettings.showDebugInfo;
                    renderClotheslineNetwork.render(world, manager.getNetworkEdges(), event.getCamera(), x, y, z, partialTicks);
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
                IClientNetworkManager manager = world.getCapability(Clothesline.CLIENT_NETWORK_MANAGER_CAPABILITY, null);
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

    @SubscribeEvent
    public void onGetMouseOver(GetMouseOverEvent event) {
        float partialTicks = event.getPartialTicks();
        Minecraft mc = Minecraft.getMinecraft();
        World world = mc.world;
        Entity renderViewEntity = mc.getRenderViewEntity();
        if (world != null && renderViewEntity != null) {
            IClientNetworkManager manager = world.getCapability(Clothesline.CLIENT_NETWORK_MANAGER_CAPABILITY, null);
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
    private NetworkRaytraceHit raytraceNetworks(IClientNetworkManager manager, Ray ray, double maxDistanceSq, float partialTicks) {
        Box box = Box.create(
            (int) Math.floor(Math.min(ray.from.x, ray.to.x) - 0.5D),
            (int) Math.floor(Math.min(ray.from.y, ray.to.y) - 0.5D),
            (int) Math.floor(Math.min(ray.from.z, ray.to.z) - 0.5D),
            (int) Math.ceil(Math.max(ray.from.x, ray.to.x) + 0.5D),
            (int) Math.ceil(Math.max(ray.from.y, ray.to.y) + 0.5D),
            (int) Math.ceil(Math.max(ray.from.z, ray.to.z) + 0.5D)
        );

        NetworkRaytraceHit hit = null;
        for (Entry<IClientNetworkEdge> entry : manager.getNetworkEdges().search(box)) {
            NetworkRaytraceHit hitCandidate = raytraceEdge(ray, entry.getValue(), maxDistanceSq, partialTicks);
            if (hitCandidate != null && hitCandidate.distanceSq < maxDistanceSq) {
                maxDistanceSq = hitCandidate.distanceSq;
                hit = hitCandidate;
            }
        }

        return hit;
    }

    @Nullable
    private NetworkRaytraceHit raytraceEdge(Ray viewRay, IClientNetworkEdge edge, double maxDistanceSq, float partialTicks) {
        Graph.Edge graphEdge = edge.getGraphEdge();
        NetworkRaytraceHit hit = null;

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
                    double offset = graphEdge.getFromOffset() * (1.0D - edgeDeltaScalar) + graphEdge.getToOffset() * edgeDeltaScalar;
                    hit = new EdgeRaytraceHit(rayLengthSquared, edge, offset);
                }
            }
        }

        AbsoluteNetworkState state = edge.getNetwork().getState();
        double fromAttachmentKey = state.offsetToAttachmentKey(graphEdge.getFromOffset(), partialTicks);
        double toAttachmentKey = state.offsetToAttachmentKey(graphEdge.getToOffset(), partialTicks);
        List<MutableSortedIntMap.Entry<ItemStack>> attachments = state.getAttachmentsInRange((int) fromAttachmentKey, (int) toAttachmentKey);
        if (!attachments.isEmpty()) {
            Vector4f lFrom = new Vector4f();
            Vector4f lTo = new Vector4f();
            Vector4f wHitVec = new Vector4f();

            EdgeAttachmentProjector projector = EdgeAttachmentProjector.build(edge);
            for (MutableSortedIntMap.Entry<ItemStack> entry : attachments) {
                double attachmentOffset = state.attachmentKeyToOffset(entry.getKey(), partialTicks);
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
                        hit = new AttachmentRaytraceHit(distanceSq, edge, entry.getKey(), l2w);
                    }
                }
            }
        }

        return hit;
    }
}
