package net.michanide.overloadgenerators.init;

import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.registration.impl.ContainerTypeDeferredRegister;
import mekanism.common.registration.impl.ContainerTypeRegistryObject;
import net.michanide.overloadgenerators.OverloadGenerators;
import net.michanide.overloadgenerators.tile.BlockEntityCPUUsageGenerator;
import net.michanide.overloadgenerators.tile.BlockEntityTickTimeGenerator;

public class OverGenContainerTypes {
    
    private OverGenContainerTypes() {
    }

    public static final ContainerTypeDeferredRegister CONTAINER_TYPES = new ContainerTypeDeferredRegister(OverloadGenerators.MOD_ID);

    public static final ContainerTypeRegistryObject<MekanismTileContainer<BlockEntityCPUUsageGenerator>> CPU_USAGE_GENERATOR = CONTAINER_TYPES.register("cpu_usage_generator", BlockEntityCPUUsageGenerator.class);
    public static final ContainerTypeRegistryObject<MekanismTileContainer<BlockEntityTickTimeGenerator>> TICK_TIME_GENERATOR = CONTAINER_TYPES.register("tick_time_generator", BlockEntityTickTimeGenerator.class);
}
