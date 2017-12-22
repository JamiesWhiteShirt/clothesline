package com.jamieswhiteshirt.clothesline.core;

import com.jamieswhiteshirt.clothesline.core.event.GetMouseOverEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientHooks {
    public static void onGetMouseOver(float partialTicks) {
        MinecraftForge.EVENT_BUS.post(new GetMouseOverEvent(partialTicks));
    }
}
