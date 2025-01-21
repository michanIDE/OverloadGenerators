package net.michanide.overloadgenerators.init;

import net.michanide.overloadgenerators.OverloadGenerators;
import net.michanide.overloadgenerators.blocks.tile.BlockEntityOverheatGenerator;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntity {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, OverloadGenerators.MOD_ID);

    public static final RegistryObject<BlockEntityType<BlockEntityOverheatGenerator>> OVERHEAT_GENERATOR = BLOCK_ENTITIES.register("overheat_generator_block_entity", () -> BlockEntityType.Builder.of(BlockEntityOverheatGenerator::new, ModBlock.OVERHEAT_GENERATOR.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
