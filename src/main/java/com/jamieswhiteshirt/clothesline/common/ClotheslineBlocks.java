package com.jamieswhiteshirt.clothesline.common;

import com.jamieswhiteshirt.clothesline.Clothesline;
import com.jamieswhiteshirt.clothesline.common.block.BlockClotheslineAnchor;
import net.minecraft.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@GameRegistry.ObjectHolder(Clothesline.MODID)
public class ClotheslineBlocks {
    public static final BlockClotheslineAnchor CLOTHESLINE_ANCHOR = Util.nonNullInjected();

    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(
            new BlockClotheslineAnchor().setTranslationKey("clothesline.clotheslineAnchor").setRegistryName(Clothesline.MODID, "clothesline_anchor").setHardness(0.2F).setCreativeTab(Clothesline.creativeTab)
        );
    }
}
