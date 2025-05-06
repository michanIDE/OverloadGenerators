package net.michanide.overloadgenerators;

import mekanism.api.text.ILangEntry;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;

public enum OverloadGeneratorsLang implements ILangEntry {
    CPU_USAGE("gui", "cpu_usage_ratio"),
    CORES("gui", "cores"),
    GENERIC_MILLISECONDS("gui", "generic_milliseconds"),
    GENERIC_NANOSECONDS("gui", "generic_nanoseconds"),
    TICK_TIME("gui", "tick_time"),

    CPU_USAGE_GENERATOR_DESCRIPTION("description", "cpu_usage_generator"),
    TICK_TIME_GENERATOR_DESCRIPTION("description", "tick_time_generator"),
    OPTICAL_LATTICE_CLOCK_DESCRIPTION("description", "optical_lattice_clock");

    private final String key;

    OverloadGeneratorsLang(String type, String path) {
        this(Util.makeDescriptionId(type, new ResourceLocation(OverloadGenerators.MOD_ID, path)));
    }

    OverloadGeneratorsLang(String key) {
        this.key = key;
    }

    @Override
    public String getTranslationKey() {
        return key;
    }
    
}
