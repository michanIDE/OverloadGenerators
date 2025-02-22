package net.michanide.overloadgenerators.init;

import mekanism.common.block.attribute.Attributes;
import mekanism.generators.common.GeneratorsLang;
import mekanism.generators.common.content.blocktype.Generator;
import mekanism.generators.common.content.blocktype.Generator.GeneratorBuilder;
import net.michanide.overloadgenerators.config.OverGenConfig;
import net.michanide.overloadgenerators.tile.BlockEntityCPUUsageGenerator;

public class OverGenBlockTypes {

    private OverGenBlockTypes() {
    }

    // Solar Generator
    public static final Generator<BlockEntityCPUUsageGenerator> CPU_USAGE_GENERATOR = GeneratorBuilder
          .createGenerator(() -> OverGenBlockEntity.CPU_USAGE_GENERATOR, GeneratorsLang.DESCRIPTION_SOLAR_GENERATOR)
          .withGui(() -> OverGenContainerTypes.CPU_USAGE_GENERATOR)
          .withEnergyConfig(OverGenConfig.config.cpuUsageGeneratorStorage)
          .withComputerSupport("oveheatGenerator")
          .replace(Attributes.ACTIVE)
          .build();
    
}
