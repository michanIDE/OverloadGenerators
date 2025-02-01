package net.michanide.overloadgenerators.init;

import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.registration.impl.ContainerTypeDeferredRegister;
import mekanism.common.registration.impl.ContainerTypeRegistryObject;
import net.michanide.overloadgenerators.OverloadGenerators;
import net.michanide.overloadgenerators.tile.BlockEntityOverheatGenerator;

public class OverGenContainerTypes {
    
    private OverGenContainerTypes() {
    }

    public static final ContainerTypeDeferredRegister CONTAINER_TYPES = new ContainerTypeDeferredRegister(OverloadGenerators.MOD_ID);

    public static final ContainerTypeRegistryObject<MekanismTileContainer<BlockEntityOverheatGenerator>> OVERHEAT_GENERATOR = CONTAINER_TYPES.register("overheat_generator", BlockEntityOverheatGenerator.class);
}
