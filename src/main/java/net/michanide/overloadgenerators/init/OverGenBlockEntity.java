package net.michanide.overloadgenerators.init;

import mekanism.common.registration.impl.TileEntityTypeDeferredRegister;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import net.michanide.overloadgenerators.OverloadGenerators;
import net.michanide.overloadgenerators.tile.BlockEntityOverheatGenerator;

public class OverGenBlockEntity {
    private OverGenBlockEntity() {
    }

    public static final TileEntityTypeDeferredRegister BLOCK_ENTITIES = new TileEntityTypeDeferredRegister(OverloadGenerators.MOD_ID);

    //Generators
    public static final TileEntityTypeRegistryObject<BlockEntityOverheatGenerator> OVERHEAT_GENERATOR = BLOCK_ENTITIES.register(OverGenBlocks.OVERHEAT_GENERATOR, BlockEntityOverheatGenerator::new);
}
