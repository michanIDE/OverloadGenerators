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
import net.michanide.overloadgenerators.tile.BlockEntityOverheatGenerator;
import net.minecraft.world.level.block.Block;
public class OverGenBlocks {

    private OverGenBlocks() {
    }

    public static final BlockDeferredRegister BLOCKS = new BlockDeferredRegister(OverloadGenerators.MOD_ID);
    public static final BlockRegistryObject<BlockTileModel<BlockEntityOverheatGenerator, Generator<BlockEntityOverheatGenerator>>, ItemBlockMachine> OVERHEAT_GENERATOR = BLOCKS.register("overheat_generator", () -> new BlockTileModel<>(OverGenBlockTypes.OVERHEAT_GENERATOR), ItemBlockMachine::new);
    
    private static <BLOCK extends Block & IHasDescription> BlockRegistryObject<BLOCK, ItemBlockTooltip<BLOCK>> registerTooltipBlock(String name, Supplier<BLOCK> blockCreator) {
        return BLOCKS.registerDefaultProperties(name, blockCreator, ItemBlockTooltip::new);
    }
}