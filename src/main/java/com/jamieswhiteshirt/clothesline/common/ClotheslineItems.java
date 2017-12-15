package com.jamieswhiteshirt.clothesline.common;

import com.jamieswhiteshirt.clothesline.Clothesline;
import com.jamieswhiteshirt.clothesline.common.item.ItemClothesline;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@GameRegistry.ObjectHolder(Clothesline.MODID)
public class ClotheslineItems {
    public static final ItemClothesline CLOTHESLINE = Util.nonNullInjected();
    public static final ItemBlock CLOTHESLINE_ANCHOR = Util.nonNullInjected();
    public static final Item CLOTHESLINE_CRANK = Util.nonNullInjected();
    public static final Item PULLEY_WHEEL = Util.nonNullInjected();

    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                new ItemClothesline().setUnlocalizedName("clothesline.clothesline").setRegistryName(Clothesline.MODID, "clothesline"),
                new ItemBlock(ClotheslineBlocks.CLOTHESLINE_ANCHOR).setRegistryName(Clothesline.MODID, "clothesline_anchor"),
                new Item().setUnlocalizedName("clothesline.clotheslineCrank").setRegistryName(Clothesline.MODID, "clothesline_crank"),
                new Item().setUnlocalizedName("clothesline.pulleyWheel").setRegistryName(Clothesline.MODID, "pulley_wheel")
        );
    }
}
