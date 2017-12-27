package com.jamieswhiteshirt.clothesline.client;

import com.jamieswhiteshirt.clothesline.Clothesline;
import com.jamieswhiteshirt.clothesline.api.AbsoluteTree;
import com.jamieswhiteshirt.clothesline.api.Network;
import com.jamieswhiteshirt.clothesline.api.INetworkManager;
import com.jamieswhiteshirt.clothesline.client.entity.EntityClotheslineHit;
import com.jamieswhiteshirt.clothesline.client.network.messagehandler.*;
import com.jamieswhiteshirt.clothesline.client.renderer.RenderClotheslineNetwork;
import com.jamieswhiteshirt.clothesline.client.renderer.RenderNetworkState;
import com.jamieswhiteshirt.clothesline.client.renderer.tileentity.TileEntityClotheslineAnchorRenderer;
import com.jamieswhiteshirt.clothesline.common.ClotheslineItems;
import com.jamieswhiteshirt.clothesline.common.CommonProxy;
import com.jamieswhiteshirt.clothesline.common.impl.NetworkManager;
import com.jamieswhiteshirt.clothesline.common.network.message.*;
import com.jamieswhiteshirt.clothesline.common.tileentity.TileEntityClotheslineAnchor;
import com.jamieswhiteshirt.clothesline.core.event.GetMouseOverEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
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

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {
    @CapabilityInject(INetworkManager.class)
    private static final Capability<INetworkManager> NETWORK_MANAGER_CAPABILITY = null;

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
    }

    @Override
    public NetworkManager createNetworkManager(World world) {
        NetworkManager manager = super.createNetworkManager(world);
        if (world instanceof WorldClient) {
            return manager;
        } else {
            return manager;
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

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        WorldClient world = Minecraft.getMinecraft().world;
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (world != null && player != null) {
            INetworkManager manager = world.getCapability(NETWORK_MANAGER_CAPABILITY, null);
            if (manager != null) {
                boolean showDebugInfo = Minecraft.getMinecraft().gameSettings.showDebugInfo;
                float partialTicks = event.getPartialTicks();
                double x = player.posX * partialTicks + player.prevPosX * (1.0F - partialTicks);
                double y = player.posY * partialTicks + player.prevPosY * (1.0F - partialTicks);
                double z = player.posZ * partialTicks + player.prevPosZ * (1.0F - partialTicks);
                for (Network network : manager.getNetworks()) {
                    //TODO: Cache the RenderNetworkStates
                    RenderNetworkState renderNetworkState = RenderNetworkState.fromNetworkState(network.getState());
                    renderClotheslineNetwork.render(world, renderNetworkState, x, y, z, partialTicks);
                    if (showDebugInfo) {
                        renderClotheslineNetwork.debugRender(renderNetworkState, x, y, z, partialTicks);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END && !Minecraft.getMinecraft().isGamePaused()) {
            WorldClient world = Minecraft.getMinecraft().world;
            if (world != null) {
                INetworkManager manager = world.getCapability(NETWORK_MANAGER_CAPABILITY, null);
                if (manager != null) {
                    manager.update();
                }
            }
        }
    }

    private class Ray {
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

    private class TreeRaytraceHit {
        public final double distanceSq;
        public final AbsoluteTree tree;
        public final AbsoluteTree.Edge edge;

        public TreeRaytraceHit(double distanceSq, AbsoluteTree tree, AbsoluteTree.Edge edge) {
            this.distanceSq = distanceSq;
            this.tree = tree;
            this.edge = edge;
        }
    }

    private class NetworkHit {
        public final TreeRaytraceHit treeHit;
        public final Network network;

        public NetworkHit(TreeRaytraceHit treeHit, Network network) {
            this.treeHit = treeHit;
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
            INetworkManager manager = world.getCapability(NETWORK_MANAGER_CAPABILITY, null);
            if (manager != null) {
                Vec3d rayFrom = renderViewEntity.getPositionEyes(partialTicks);
                Vec3d rayTo = mc.objectMouseOver.hitVec;

                //Ray ray = new Ray(rayFrom, rayFrom.add(rayTo.subtract(rayFrom).scale(0.1D)));
                Ray ray = new Ray(rayFrom, rayTo);

                NetworkHit hit = raytraceNetworks(manager, ray, ray.lengthSq);
                if (hit != null) {
                    EntityClotheslineHit pointedEntity = new EntityClotheslineHit(world, hit.treeHit.tree.getPos(), hit.treeHit.edge.getTree().getPos());
                    mc.objectMouseOver = new RayTraceResult(pointedEntity);
                    mc.pointedEntity = pointedEntity;
                }
            }
        }
    }

    @Nullable
    private NetworkHit raytraceNetworks(INetworkManager manager, Ray ray, double maxDistanceSq) {
        NetworkHit hit = null;
        for (Network network : manager.getNetworks()) {
            TreeRaytraceHit treeHit = hitTree(ray, network.getState().getTree(), maxDistanceSq);
            if (treeHit != null) {
                maxDistanceSq = treeHit.distanceSq;
                hit = new NetworkHit(treeHit, network);
            }
        }
        return hit;
    }

    private Vec3d getMiddleVec(BlockPos pos) {
        return new Vec3d(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
    }

    @Nullable
    private TreeRaytraceHit hitTree(Ray ray, AbsoluteTree tree, double maxDistanceSq) {
        TreeRaytraceHit hit = null;

        Vec3d edgeFrom = getMiddleVec(tree.getPos());
        for (AbsoluteTree.Edge edge : tree.getEdges()) {
            TreeRaytraceHit edgeHit = hitEdge(ray, tree, edgeFrom, edge, maxDistanceSq);
            if (edgeHit != null) {
                maxDistanceSq = edgeHit.distanceSq;
                hit = edgeHit;
            }

            TreeRaytraceHit childHit = hitTree(ray, edge.getTree(), maxDistanceSq);
            if (childHit != null) {
                maxDistanceSq = childHit.distanceSq;
                hit = childHit;
            }
        }

        return hit;
    }

    @Nullable
    private TreeRaytraceHit hitEdge(Ray viewRay, AbsoluteTree tree, Vec3d edgeFrom, AbsoluteTree.Edge edge, double maxDistanceSq) {
        Ray edgeRay = new Ray(edgeFrom, getMiddleVec(edge.getTree().getPos()));

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
            if (nearDelta.lengthSquared() < 0.1D * 0.1D) {
                double rayLengthSquared = (viewNear.subtract(viewRay.from)).lengthSquared();
                if (rayLengthSquared < maxDistanceSq) {
                    return new TreeRaytraceHit(rayLengthSquared, tree, edge);
                }
            }
        }

        return null;
    }
}
