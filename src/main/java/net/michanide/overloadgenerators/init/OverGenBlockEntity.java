package net.michanide.overloadgenerators.init;

import mekanism.common.registration.impl.TileEntityTypeDeferredRegister;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import net.michanide.overloadgenerators.OverloadGenerators;
import net.michanide.overloadgenerators.tile.BlockEntityCPUUsageGenerator;
import net.michanide.overloadgenerators.tile.BlockEntityTickTimeGenerator;

public class OverGenBlockEntity {
    private OverGenBlockEntity() {
    }

    public static final TileEntityTypeDeferredRegister BLOCK_ENTITIES = new TileEntityTypeDeferredRegister(OverloadGenerators.MOD_ID);

    //Generators
    public static final TileEntityTypeRegistryObject<BlockEntityCPUUsageGenerator> CPU_USAGE_GENERATOR = BLOCK_ENTITIES.register(OverGenBlocks.CPU_USAGE_GENERATOR, BlockEntityCPUUsageGenerator::new);
    public static final TileEntityTypeRegistryObject<BlockEntityTickTimeGenerator> TICK_TIME_GENERATOR = BLOCK_ENTITIES.register(OverGenBlocks.TICK_TIME_GENERATOR, BlockEntityTickTimeGenerator::new);
}
