package com.jamieswhiteshirt.clothesline.client;

import com.jamieswhiteshirt.clothesline.Clothesline;
import com.jamieswhiteshirt.clothesline.api.Network;
import com.jamieswhiteshirt.clothesline.api.INetworkManager;
import com.jamieswhiteshirt.clothesline.client.network.messagehandler.*;
import com.jamieswhiteshirt.clothesline.client.renderer.RenderClothesline;
import com.jamieswhiteshirt.clothesline.client.renderer.tileentity.TileEntityClotheslineAnchorRenderer;
import com.jamieswhiteshirt.clothesline.common.ClotheslineItems;
import com.jamieswhiteshirt.clothesline.common.CommonProxy;
import com.jamieswhiteshirt.clothesline.common.network.message.*;
import com.jamieswhiteshirt.clothesline.common.tileentity.TileEntityClotheslineAnchor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
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

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {
    @CapabilityInject(INetworkManager.class)
    private static final Capability<INetworkManager> NETWORK_MANAGER_CAPABILITY = null;

    private RenderClothesline renderClothesline;

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
        renderClothesline = new RenderClothesline(minecraft.getRenderManager(), minecraft.getRenderItem());

        Clothesline.instance.networkWrapper.registerMessage(new MessageSyncNetworksHandler(), MessageSyncNetworks.class, 0, Side.CLIENT);
        Clothesline.instance.networkWrapper.registerMessage(new MessageAddNetworkHandler(), MessageAddNetwork.class, 1, Side.CLIENT);
        Clothesline.instance.networkWrapper.registerMessage(new MessageRemoveNetworkHandler(), MessageRemoveNetwork.class, 2, Side.CLIENT);
        Clothesline.instance.networkWrapper.registerMessage(new MessageSetItemHandler(), MessageSetItem.class, 3, Side.CLIENT);
        Clothesline.instance.networkWrapper.registerMessage(new MessageRemoveAttachmentHandler(), MessageRemoveItem.class, 4, Side.CLIENT);
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
                float partialTicks = event.getPartialTicks();
                double x = player.posX * partialTicks + player.prevPosX * (1.0F - partialTicks);
                double y = player.posY * partialTicks + player.prevPosY * (1.0F - partialTicks);
                double z = player.posZ * partialTicks + player.prevPosZ * (1.0F - partialTicks);
                for (Network network : manager.getNetworks().values()) {
                    renderClothesline.render(network.getState(), x, y, z, partialTicks);
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
}
