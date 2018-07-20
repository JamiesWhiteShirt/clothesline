package com.jamieswhiteshirt.clothesline;

import com.jamieswhiteshirt.clothesline.api.*;
import com.jamieswhiteshirt.clothesline.api.client.IClientNetworkManager;
import com.jamieswhiteshirt.clothesline.common.*;
import com.jamieswhiteshirt.clothesline.common.capability.*;
import com.jamieswhiteshirt.clothesline.common.impl.Connector;
import com.jamieswhiteshirt.clothesline.common.impl.ServerNetworkManager;
import com.jamieswhiteshirt.clothesline.common.impl.ManagerSynchronizationListener;
import com.jamieswhiteshirt.clothesline.common.network.message.SetConnectorPosMessage;
import com.jamieswhiteshirt.clothesline.common.network.message.SetNetworkMessage;
import com.jamieswhiteshirt.clothesline.common.tileentity.TileEntityClotheslineAnchor;
import com.jamieswhiteshirt.clothesline.common.util.BasicNetwork;
import com.jamieswhiteshirt.clothesline.hooks.api.MayPlaceBlockEvent;
import com.jamieswhiteshirt.rtree3i.Box;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
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
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.stream.Collectors;

@Mod(
    modid = Clothesline.MODID,
    version = Clothesline.VERSION,
    name = "Clothesline",
    dependencies = Clothesline.DEPENDENCIES,
    certificateFingerprint = Clothesline.CERTIFICATE_FINGERPRINT
)
public class Clothesline {
    public static final String MODID = "clothesline";
    public static final String VERSION = "1.12.2-1.0.0.0-SNAPSHOT";
    public static final String DEPENDENCIES = "required-after:clothesline-hooks;required-after:forge@[14.23.3.2665,)";
    public static final String CERTIFICATE_FINGERPRINT = "3bae2d07b93a5971335cb2de15230c19c103db32";

    @CapabilityInject(INetworkManager.class)
    public static final Capability<INetworkManager<?, ?>> NETWORK_MANAGER_CAPABILITY = Util.nonNullInjected();
    @CapabilityInject(IServerNetworkManager.class)
    public static final Capability<IServerNetworkManager> SERVER_NETWORK_MANAGER_CAPABILITY = Util.nonNullInjected();
    @CapabilityInject(IClientNetworkManager.class)
    public static final Capability<IClientNetworkManager> CLIENT_NETWORK_MANAGER_CAPABILITY = Util.nonNullInjected();
    @CapabilityInject(IConnector.class)
    public static final Capability<IConnector> CONNECTOR_CAPABILITY = Util.nonNullInjected();

    @Mod.Instance
    public static Clothesline instance;
    @SidedProxy(
        clientSide = "com.jamieswhiteshirt.clothesline.client.ClientProxy",
        serverSide = "com.jamieswhiteshirt.clothesline.server.ServerProxy",
        modId = MODID
    )
    public static CommonProxy proxy;

    public static final Logger logger = LogManager.getLogger(MODID);

    public SimpleNetworkWrapper networkChannel;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        CapabilityManager.INSTANCE.register(INetworkManager.class, new DummyStorage<>(), new DummyFactory<>());
        CapabilityManager.INSTANCE.register(IServerNetworkManager.class, new DummyStorage<>(), new DummyFactory<>());
        CapabilityManager.INSTANCE.register(IClientNetworkManager.class, new DummyStorage<>(), new DummyFactory<>());
        CapabilityManager.INSTANCE.register(IConnector.class, new ConnectorStorage(), Connector::new);

        networkChannel = proxy.createNetworkChannel();
        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @EventHandler
    public void onFingerprintViolation(FMLFingerprintViolationEvent event) {
        logger.warn("The file " + event.getSource().getName() + " may have been tampered with.");
        logger.warn("This version is NOT supported by the author.");
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

    private static final ResourceLocation SYNCHRONIZATION_KEY = new ResourceLocation(MODID, "synchronization");

    @SubscribeEvent
    public void attachWorldCapabilities(AttachCapabilitiesEvent<World> event) {
        World world = event.getObject();
        if (world instanceof WorldServer) {
            ServerNetworkManager manager = new ServerNetworkManager((WorldServer) world);
            manager.addEventListener(SYNCHRONIZATION_KEY, new ManagerSynchronizationListener<>((WorldServer) world, Clothesline.instance.networkChannel));
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
            networkChannel.sendTo(new SetConnectorPosMessage(target.getEntityId(), connector.getPos()), (EntityPlayerMP) event.getEntityPlayer());
        }
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (!event.getWorld().isRemote && event.getEntity() instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) event.getEntity();
            INetworkManager<?, ?> manager = event.getWorld().getCapability(NETWORK_MANAGER_CAPABILITY, null);
            if (manager != null) {
                networkChannel.sendTo(new SetNetworkMessage(manager.getNetworks().stream().map(
                        BasicNetwork::fromAbsolute
                ).collect(Collectors.toList())), player);
            }
        }
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            INetworkManager manager = event.world.getCapability(NETWORK_MANAGER_CAPABILITY, null);
            if (manager != null) {
                manager.update();
            }
        }
    }

    @SubscribeEvent
    public void onMayPlaceBlock(MayPlaceBlockEvent event) {
        AxisAlignedBB blockAabb = event.getState().getCollisionBoundingBox(event.getWorld(), event.getPos());
        if (blockAabb != Block.NULL_AABB) {
            INetworkManager<?, ?> manager = event.getWorld().getCapability(NETWORK_MANAGER_CAPABILITY, null);
            if (manager != null) {
                BlockPos pos = event.getPos();
                AxisAlignedBB aabb = blockAabb.offset(pos);
                Box box = Box.create(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
                boolean intersects = manager.getEdges()
                    .values(box::intersects)
                    .anyMatch(networkEdge -> {
                        Line line = networkEdge.getGraphEdge().getLine();
                        return aabb.calculateIntercept(line.getFromVec(), line.getToVec()) != null;
                    });
                if (intersects) {
                    event.setCanceled(true);
                }
            }
        }
    }
}
