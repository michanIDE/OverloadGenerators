package net.michanide.overloadgenerators;

import com.mojang.logging.LogUtils;

import mekanism.common.config.MekanismModConfig;
import mekanism.generators.common.config.MekanismGeneratorsConfig;
import net.michanide.overloadgenerators.config.OverGenConfig;
import net.michanide.overloadgenerators.init.OverGenBlockEntity;
import net.michanide.overloadgenerators.init.OverGenBlocks;
import net.michanide.overloadgenerators.init.OverGenContainerTypes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;


// The value here should match an entry in the META-INF/mods.toml file
@Mod(OverloadGenerators.MOD_ID)
public class OverloadGenerators
{
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final String MOD_ID = "overloadgenerators";
    public static final String MOD_NAME = "Overload Generators";
    public static final String MOD_NAME_SAFE = "Overload_Generators";

    public OverloadGenerators()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        OverGenConfig.register(ModLoadingContext.get());
        OverGenBlocks.BLOCKS.register(modEventBus);
        OverGenBlockEntity.BLOCK_ENTITIES.register(modEventBus);

        OverGenContainerTypes.CONTAINER_TYPES.register(modEventBus);

        // modEventBus.addListener(this::clientSetup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void onConfigLoad(ModConfigEvent configEvent) {
        //Note: We listen to both the initial load and the reload, to make sure that we fix any accidentally
        // cached values from calls before the initial loading
        ModConfig config = configEvent.getConfig();
        //Make sure it is for the same modid as us
        if (config.getModId().equals(MOD_ID) && config instanceof MekanismModConfig mekConfig) {
            mekConfig.clearCache();
        }
    }
}
