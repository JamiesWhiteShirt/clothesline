package com.jamieswhiteshirt.clothesline;

import com.jamieswhiteshirt.clothesline.api.*;
import com.jamieswhiteshirt.clothesline.common.*;
import com.jamieswhiteshirt.clothesline.common.capability.*;
import com.jamieswhiteshirt.clothesline.common.impl.Connector;
import com.jamieswhiteshirt.clothesline.common.impl.CommonNetworkManager;
import com.jamieswhiteshirt.clothesline.common.impl.ServerNetworkManager;
import com.jamieswhiteshirt.clothesline.common.impl.SynchronizationListener;
import com.jamieswhiteshirt.clothesline.common.network.message.MessageSetConnectorPos;
import com.jamieswhiteshirt.clothesline.common.network.message.MessageSetNetworks;
import com.jamieswhiteshirt.clothesline.common.tileentity.TileEntityClotheslineAnchor;
import com.jamieswhiteshirt.clothesline.common.util.BasicNetwork;
import com.jamieswhiteshirt.clothesline.common.util.BasicPersistentNetwork;
import com.jamieswhiteshirt.clothesline.core.event.MayPlaceBlockEvent;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.Logger;

import java.util.stream.Collectors;

@Mod(
        modid = Clothesline.MODID,
        version = Clothesline.VERSION,
        name = "Clothesline",
        dependencies = "required-after:clothesline_hooks"
)
public class Clothesline {
    public static final String MODID = "clothesline";
    public static final String VERSION = "1.12-0.0.0.0";

    @CapabilityInject(ICommonNetworkManager.class)
    private static final Capability<ICommonNetworkManager> NETWORK_MANAGER_CAPABILITY = Util.nonNullInjected();
    @CapabilityInject(IConnector.class)
    private static final Capability<IConnector> CONNECTOR_CAPABILITY = Util.nonNullInjected();

    @Mod.Instance
    public static Clothesline instance;
    @SidedProxy(
            clientSide = "com.jamieswhiteshirt.clothesline.client.ClientProxy",
            serverSide = "com.jamieswhiteshirt.clothesline.server.ServerProxy",
            modId = MODID
    )
    public static CommonProxy proxy;

    public static Logger logger;

    public final SimpleNetworkWrapper networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        MinecraftForge.EVENT_BUS.register(this);
        CapabilityManager.INSTANCE.register(ICommonNetworkManager.class, new DummyStorage<>(), CommonNetworkManager.class);
        CapabilityManager.INSTANCE.register(IServerNetworkManager.class, new ServerNetworkManagerStorage(), ServerNetworkManager.class);
        CapabilityManager.INSTANCE.register(IConnector.class, new ConnectorStorage(), Connector::new);
        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        ClotheslineBlocks.registerBlocks(event);

        GameRegistry.registerTileEntity(TileEntityClotheslineAnchor.class, "clothesline:clothesline_anchor");
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        ClotheslineItems.registerItems(event);
    }

    @SubscribeEvent
    public void registerSoundEvents(RegistryEvent.Register<SoundEvent> event) {
        ClotheslineSoundEvents.registerSoundEvents(event);
    }

    @SubscribeEvent
    public void attachWorldCapabilities(AttachCapabilitiesEvent<World> event) {
        World world = event.getObject();
        if (world instanceof WorldServer) {
            ServerNetworkManager manager = new ServerNetworkManager((WorldServer) world);
            manager.addEventListener(new SynchronizationListener((WorldServer) world, Clothesline.instance.networkWrapper));
            event.addCapability(new ResourceLocation(MODID, "network_manager"), new ServerNetworkManagerProvider(manager));
        }
    }

    @SubscribeEvent
    public void attachEntityCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof EntityPlayer) {
            event.addCapability(new ResourceLocation(MODID, "connector"), new ConnectorProvider());
        }
    }

    @SubscribeEvent
    public void onPlayerStartTracking(PlayerEvent.StartTracking event) {
        Entity target = event.getTarget();
        IConnector connector = target.getCapability(CONNECTOR_CAPABILITY, null);
        if (connector != null) {
            networkWrapper.sendTo(new MessageSetConnectorPos(target.getEntityId(), connector.getPos()), (EntityPlayerMP) event.getEntityPlayer());
        }
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (!event.getWorld().isRemote && event.getEntity() instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) event.getEntity();
            ICommonNetworkManager manager = event.getWorld().getCapability(NETWORK_MANAGER_CAPABILITY, null);
            if (manager != null) {
                networkWrapper.sendTo(new MessageSetNetworks(manager.getNetworks().stream().map(
                        BasicNetwork::fromAbsolute
                ).collect(Collectors.toList())), player);
            }
        }
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            ICommonNetworkManager manager = event.world.getCapability(NETWORK_MANAGER_CAPABILITY, null);
            if (manager != null) {
                manager.update();
            }
        }
    }

    @SubscribeEvent
    public void onMayPlaceBlock(MayPlaceBlockEvent event) {
        AxisAlignedBB aabb = event.getState().getCollisionBoundingBox(event.getWorld(), event.getPos());
        if (aabb != Block.NULL_AABB) {
            ICommonNetworkManager manager = event.getWorld().getCapability(NETWORK_MANAGER_CAPABILITY, null);
            if (manager != null) {
                for (Network network : manager.getNetworks()) {
                    if (intersectsTree(aabb.offset(event.getPos()), network.getState().getTree())) {
                        event.setCanceled(true);
                        return;
                    }
                }
            }
        }
    }

    private boolean intersectsTree(AxisAlignedBB aabb, AbsoluteTree parent) {
        Vec3d parentVec = Measurements.midVec(parent.getPos());
        for (AbsoluteTree.Edge edge : parent.getEdges()) {
            AbsoluteTree child = edge.getTree();
            Vec3d childVec = Measurements.midVec(child.getPos());
            if (aabb.calculateIntercept(parentVec, childVec) != null || intersectsTree(aabb, child)) {
                return true;
            }
        }
        return false;
    }
}
