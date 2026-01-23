package net.michanide.overloadgenerators.init;

import mekanism.common.registration.impl.TileEntityTypeDeferredRegister;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import net.michanide.overloadgenerators.OverloadGenerators;
import net.michanide.overloadgenerators.tile.BlockEntityCPUUsageGenerator;
import net.michanide.overloadgenerators.tile.BlockEntityMemoryUsageGenerator;
import net.michanide.overloadgenerators.tile.BlockEntityTickTimeGenerator;
import net.michanide.overloadgenerators.tile.BlockEntityOpticalLatticeClock;
import net.michanide.overloadgenerators.tile.BlockEntityServerCrashGenerator;

public class OverGenBlockEntity {
    private OverGenBlockEntity() {
    }

    public static final TileEntityTypeDeferredRegister BLOCK_ENTITIES = new TileEntityTypeDeferredRegister(OverloadGenerators.MOD_ID);

    //Generators
    public static final TileEntityTypeRegistryObject<BlockEntityCPUUsageGenerator> CPU_USAGE_GENERATOR = BLOCK_ENTITIES.register(OverGenBlocks.CPU_USAGE_GENERATOR, BlockEntityCPUUsageGenerator::new);
    public static final TileEntityTypeRegistryObject<BlockEntityMemoryUsageGenerator> MEMORY_USAGE_GENERATOR = BLOCK_ENTITIES.register(OverGenBlocks.MEMORY_USAGE_GENERATOR, BlockEntityMemoryUsageGenerator::new);
    public static final TileEntityTypeRegistryObject<BlockEntityTickTimeGenerator> TICK_TIME_GENERATOR = BLOCK_ENTITIES.register(OverGenBlocks.TICK_TIME_GENERATOR, BlockEntityTickTimeGenerator::new);
    public static final TileEntityTypeRegistryObject<BlockEntityServerCrashGenerator> SERVER_CRASH_GENERATOR = BLOCK_ENTITIES.register(OverGenBlocks.SERVER_CRASH_GENERATOR, BlockEntityServerCrashGenerator::new);

    //Misc
    public static final TileEntityTypeRegistryObject<BlockEntityOpticalLatticeClock> OPTICAL_LATTICE_CLOCK = BLOCK_ENTITIES.register(OverGenBlocks.OPTICAL_LATTICE_CLOCK, BlockEntityOpticalLatticeClock::new);
}
