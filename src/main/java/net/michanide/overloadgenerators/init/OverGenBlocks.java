package net.michanide.overloadgenerators.init;

import java.util.function.Supplier;

import mekanism.common.block.interfaces.IHasDescription;
import mekanism.common.block.prefab.BlockTile.BlockTileModel;
import mekanism.common.item.block.ItemBlockTooltip;
import mekanism.common.item.block.machine.ItemBlockMachine;
import mekanism.common.registration.impl.BlockDeferredRegister;
import mekanism.common.registration.impl.BlockRegistryObject;
import mekanism.generators.common.content.blocktype.Generator;
import net.michanide.overloadgenerators.OverloadGenerators;
import net.michanide.overloadgenerators.block.BlockOpticalLatticeClock;
import net.michanide.overloadgenerators.tile.BlockEntityCPUUsageGenerator;
import net.michanide.overloadgenerators.tile.BlockEntityTickTimeGenerator;
import net.minecraft.world.level.block.Block;
public class OverGenBlocks {

    private OverGenBlocks() {
    }

    public static final BlockDeferredRegister BLOCKS = new BlockDeferredRegister(OverloadGenerators.MOD_ID);
    public static final BlockRegistryObject<BlockTileModel<BlockEntityCPUUsageGenerator, Generator<BlockEntityCPUUsageGenerator>>, ItemBlockMachine> CPU_USAGE_GENERATOR = BLOCKS.register("cpu_usage_generator", () -> new BlockTileModel<>(OverGenBlockTypes.CPU_USAGE_GENERATOR), ItemBlockMachine::new);
    public static final BlockRegistryObject<BlockTileModel<BlockEntityTickTimeGenerator, Generator<BlockEntityTickTimeGenerator>>, ItemBlockMachine> TICK_TIME_GENERATOR = BLOCKS.register("tick_time_generator", () -> new BlockTileModel<>(OverGenBlockTypes.TICK_TIME_GENERATOR), ItemBlockMachine::new);

    public static final BlockRegistryObject<BlockOpticalLatticeClock, ItemBlockTooltip<BlockOpticalLatticeClock>> OPTICAL_LATTICE_CLOCK = BLOCKS.register("optical_lattice_clock", BlockOpticalLatticeClock::new, ItemBlockTooltip<BlockOpticalLatticeClock>::new);
    
    private static <BLOCK extends Block & IHasDescription> BlockRegistryObject<BLOCK, ItemBlockTooltip<BLOCK>> registerTooltipBlock(String name, Supplier<BLOCK> blockCreator) {
        return BLOCKS.registerDefaultProperties(name, blockCreator, ItemBlockTooltip::new);
    }
}