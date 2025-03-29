package net.michanide.overloadgenerators.init;

import mekanism.common.block.attribute.Attributes;
import mekanism.generators.common.content.blocktype.Generator;
import mekanism.generators.common.content.blocktype.Generator.GeneratorBuilder;
import net.michanide.overloadgenerators.OverloadGeneratorsLang;
import net.michanide.overloadgenerators.config.OverGenConfig;
import net.michanide.overloadgenerators.tile.BlockEntityCPUUsageGenerator;
import net.michanide.overloadgenerators.tile.BlockEntityTickTimeGenerator;

public class OverGenBlockTypes {

    private OverGenBlockTypes() {
    }

    // CPU Usage Generator
    public static final Generator<BlockEntityCPUUsageGenerator> CPU_USAGE_GENERATOR = GeneratorBuilder
          .createGenerator(() -> OverGenBlockEntity.CPU_USAGE_GENERATOR, OverloadGeneratorsLang.CPU_USAGE_GENERATOR_DESCRIPTION)
          .withGui(() -> OverGenContainerTypes.CPU_USAGE_GENERATOR)
          .withEnergyConfig(OverGenConfig.config.cpuUsageGeneratorStorage)
          .withComputerSupport("cpuUsageGenerator")
          .replace(Attributes.ACTIVE)
          .build();
    
    // Tick Time Generator
    public static final Generator<BlockEntityTickTimeGenerator> TICK_TIME_GENERATOR = GeneratorBuilder
          .createGenerator(() -> OverGenBlockEntity.TICK_TIME_GENERATOR, OverloadGeneratorsLang.TICK_TIME_GENERATOR_DESCRIPTION)
          .withGui(() -> OverGenContainerTypes.TICK_TIME_GENERATOR)
          .withEnergyConfig(OverGenConfig.config.tickTimeGeneratorStorage)
          .withComputerSupport("tickTimeGenerator")
          .replace(Attributes.ACTIVE)
          .build();
}
