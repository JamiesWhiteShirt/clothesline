package com.jamieswhiteshirt.clothesline.core.plugin;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import javax.annotation.Nullable;
import java.util.Map;

@IFMLLoadingPlugin.Name("clothesline_hooks")
@IFMLLoadingPlugin.SortingIndex(1001)
@IFMLLoadingPlugin.TransformerExclusions("com.jamieswhiteshirt.clothesline.core.plugin")
public class FMLLoadingPlugin implements IFMLLoadingPlugin {
    @Override
    public String[] getASMTransformerClass() {
        return new String[] { "com.jamieswhiteshirt.clothesline.core.plugin.ClassTransformer" };
    }

    @Override
    public String getModContainerClass() {
        return "com.jamieswhiteshirt.clothesline.core.ClotheslineHooks";
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
