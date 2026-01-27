package net.michanide.overloadgenerators;

import mekanism.common.config.MekanismModConfig;

import net.michanide.overloadgenerators.config.OverGenConfig;
import net.michanide.overloadgenerators.init.OverGenBlockEntity;
import net.michanide.overloadgenerators.init.OverGenBlocks;
import net.michanide.overloadgenerators.init.OverGenContainerTypes;
import net.michanide.overloadgenerators.init.OverGenCreativeTabs;
import net.michanide.overloadgenerators.init.OverGenItems;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(OverloadGenerators.MOD_ID)
public class OverloadGenerators
{

    public static final String MOD_ID = "overloadgenerators";
    public static final String MOD_NAME = "Overload Generators";
    public static final String MOD_NAME_SAFE = "Overload_Generators";

    public OverloadGenerators()
    {

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::onConfigLoad);

        OverGenConfig.register(ModLoadingContext.get());
        OverGenItems.ITEMS.register(modEventBus);
        OverGenBlocks.BLOCKS.register(modEventBus);
        OverGenBlockEntity.BLOCK_ENTITIES.register(modEventBus);
        OverGenCreativeTabs.CREATIVE_TABS.register(modEventBus);

        OverGenContainerTypes.CONTAINER_TYPES.register(modEventBus);
    }

    private void onConfigLoad(ModConfigEvent configEvent) {
        ModConfig config = configEvent.getConfig();
        if (config.getModId().equals(MOD_ID) && config instanceof MekanismModConfig overgenConfig) {
            overgenConfig.clearCache(configEvent);
        }
    }
}
