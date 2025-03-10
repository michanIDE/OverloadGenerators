package net.michanide.overloadgenerators;

import mekanism.api.text.ILangEntry;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;

public enum OverloadGeneratorsLang implements ILangEntry {
    CPU_USAGE("gui", "cpu_usage_ratio"),

    CPU_USAGE_GENERATOR_DESCRIPTION("description", "cpu_usage_generator");

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
