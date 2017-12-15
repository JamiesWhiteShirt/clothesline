package com.jamieswhiteshirt.clothesline.common;

import com.jamieswhiteshirt.clothesline.Clothesline;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@GameRegistry.ObjectHolder(Clothesline.MODID)
public class ClotheslineSoundEvents {
    @GameRegistry.ObjectHolder("block.clothesline_anchor.squeak")
    public static final SoundEvent BLOCK_CLOTHESLINE_ANCHOR_SQUEAK = Util.nonNullInjected();
    @GameRegistry.ObjectHolder("block.clothesline_anchor.rope")
    public static final SoundEvent BLOCK_CLOTHESLINE_ANCHOR_ROPE = Util.nonNullInjected();

    private static SoundEvent createSoundEvent(String name) {
        ResourceLocation resourceLocation = new ResourceLocation(Clothesline.MODID, name);
        return new SoundEvent(resourceLocation).setRegistryName(resourceLocation);
    }

    public static void registerSoundEvents(RegistryEvent.Register<SoundEvent> event) {
        event.getRegistry().registerAll(
                createSoundEvent("block.clothesline_anchor.squeak"),
                createSoundEvent("block.clothesline_anchor.rope")
        );
    }
}
