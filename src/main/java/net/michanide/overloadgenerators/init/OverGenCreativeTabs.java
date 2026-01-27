package net.michanide.overloadgenerators.init;

import mekanism.common.registration.impl.CreativeTabDeferredRegister;
import mekanism.common.registration.impl.CreativeTabRegistryObject;
import net.michanide.overloadgenerators.OverloadGenerators;
import net.michanide.overloadgenerators.OverloadGeneratorsLang;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;

public class OverGenCreativeTabs {

    public static final CreativeTabDeferredRegister CREATIVE_TABS = new CreativeTabDeferredRegister(OverloadGenerators.MOD_ID, OverGenCreativeTabs::addToExistingTabs);

    public static final CreativeTabRegistryObject OVERGEN = CREATIVE_TABS.registerMain(OverloadGeneratorsLang.MOD_NAME, OverGenBlocks.SERVER_CRASH_GENERATOR, builder ->
        builder.displayItems((params, output) -> {
            CreativeTabDeferredRegister.addToDisplay(OverGenBlocks.BLOCKS, output);
            CreativeTabDeferredRegister.addToDisplay(OverGenItems.ITEMS, output);
        }));
    
    private static void addToExistingTabs(BuildCreativeModeTabContentsEvent event) {
        ResourceKey<CreativeModeTab> tabKey = event.getTabKey();
        if (tabKey == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            CreativeTabDeferredRegister.addToDisplay(event, OverGenBlocks.CPU_USAGE_GENERATOR, OverGenBlocks.MEMORY_USAGE_GENERATOR,
                OverGenBlocks.TICK_TIME_GENERATOR, OverGenBlocks.SERVER_CRASH_GENERATOR, OverGenBlocks.OPTICAL_LATTICE_CLOCK);
        }
    }
}
