package com.jamieswhiteshirt.clothesline.common;

import com.jamieswhiteshirt.clothesline.Clothesline;
import com.jamieswhiteshirt.clothesline.common.item.ItemClothesline;
import com.jamieswhiteshirt.clothesline.common.item.ItemSpinner;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@GameRegistry.ObjectHolder(Clothesline.MODID)
public class ClotheslineItems {
    public static final ItemClothesline CLOTHESLINE = Util.nonNullInjected();
    public static final ItemBlock CLOTHESLINE_ANCHOR = Util.nonNullInjected();
    public static final Item CRANK = Util.nonNullInjected();
    public static final Item SPINNER = Util.nonNullInjected();

    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
            new ItemClothesline().setTranslationKey("clothesline.clothesline").setRegistryName(Clothesline.MODID, "clothesline").setCreativeTab(Clothesline.creativeTab),
            new ItemBlock(ClotheslineBlocks.CLOTHESLINE_ANCHOR).setRegistryName(Clothesline.MODID, "clothesline_anchor"),
            new Item().setTranslationKey("clothesline.crank").setRegistryName(Clothesline.MODID, "crank").setCreativeTab(Clothesline.creativeTab),
            new ItemSpinner().setTranslationKey("clothesline.spinner").setRegistryName(Clothesline.MODID, "spinner").setCreativeTab(Clothesline.creativeTab)
        );
    }
}
