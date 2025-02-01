package net.michanide.overloadgenerators.init;

import mekanism.common.block.attribute.Attributes;
import mekanism.generators.common.GeneratorsLang;
import mekanism.generators.common.content.blocktype.Generator;
import mekanism.generators.common.content.blocktype.Generator.GeneratorBuilder;
import net.michanide.overloadgenerators.config.OverGenConfig;
import net.michanide.overloadgenerators.tile.BlockEntityOverheatGenerator;

public class OverGenBlockTypes {

    private OverGenBlockTypes() {
    }

    // Solar Generator
    public static final Generator<BlockEntityOverheatGenerator> OVERHEAT_GENERATOR = GeneratorBuilder
          .createGenerator(() -> OverGenBlockEntity.OVERHEAT_GENERATOR, GeneratorsLang.DESCRIPTION_SOLAR_GENERATOR)
          .withGui(() -> OverGenContainerTypes.OVERHEAT_GENERATOR)
          .withEnergyConfig(OverGenConfig.config.overheatGeneratorStorage)
          .replace(Attributes.ACTIVE)
          .build();
    
}
