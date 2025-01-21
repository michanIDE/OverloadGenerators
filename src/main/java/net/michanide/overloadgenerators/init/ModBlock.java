package net.michanide.overloadgenerators.init;

import net.michanide.overloadgenerators.OverloadGenerators;
import net.michanide.overloadgenerators.blocks.BlockOverheatGenerator;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlock {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, OverloadGenerators.MOD_ID);

    public static final RegistryObject<Block> OVERHEAT_GENERATOR = BLOCKS.register("overheat_generator", () -> new BlockOverheatGenerator(Block.Properties.of(Material.METAL).strength(3.0F).sound(SoundType.NETHERITE_BLOCK)));

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}