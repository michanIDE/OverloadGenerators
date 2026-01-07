package net.michanide.overloadgenerators.config;

import mekanism.common.config.MekanismConfigHelper;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingContext;

public class OverGenConfig {
    private OverGenConfig() {
    }

    public static final ConfigDetail config = new ConfigDetail();

    public static void register(ModLoadingContext modLoadingContext) {
        ModContainer modContainer = modLoadingContext.getActiveContainer();
       MekanismConfigHelper.registerConfig(modContainer, config);
    }
}
