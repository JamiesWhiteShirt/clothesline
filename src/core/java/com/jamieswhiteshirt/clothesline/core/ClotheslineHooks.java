package com.jamieswhiteshirt.clothesline.core;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.jamieswhiteshirt.clothesline.api.IActivityMovement;
import com.jamieswhiteshirt.clothesline.core.impl.ActivityMovement;
import com.jamieswhiteshirt.clothesline.core.impl.ActivityMovementStorage;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.Collections;

public class ClotheslineHooks extends DummyModContainer {
    public static final String MODID = "clothesline_hooks";
    public static final String VERSION = "1.12-0.0.0.0";

    public ClotheslineHooks() {
        super(new ModMetadata());
        ModMetadata metadata = getMetadata();
        metadata.modId = MODID;
        metadata.name = "Clothesline Hooks";
        metadata.version = VERSION;
        metadata.authorList = Collections.singletonList("JamiesWhiteShirt");
        metadata.description =
                "This is one of those evil core mods, known for burning down your cat and killing your house!\n" +
                "You'll find that Clothesline Hooks actually only adds some necessities to make Clothesline work.";
        metadata.screenshots = new String[0];
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller) {
        bus.register(this);
        return true;
    }

    @Subscribe
    public void preInit(FMLPreInitializationEvent evt) {
        CapabilityManager.INSTANCE.register(IActivityMovement.class, new ActivityMovementStorage(), ActivityMovement::new);
    }
}
