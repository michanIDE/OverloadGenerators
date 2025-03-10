package net.michanide.overloadgenerators.config;

import mekanism.api.math.FloatingLong;
import mekanism.common.config.BaseMekanismConfig;
import mekanism.common.config.value.CachedDoubleValue;
import mekanism.common.config.value.CachedFloatingLongValue;
import mekanism.common.config.value.CachedLongValue;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig.Type;


public class ConfigDetail extends BaseMekanismConfig {

    private final ForgeConfigSpec configSpec;

    public final CachedFloatingLongValue cpuUsageGeneratorStorage;
    public final CachedFloatingLongValue cpuUsageGeneratorGeneration;
    public final CachedDoubleValue cpuUsageGeneratorThreshold;

    ConfigDetail() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.comment("Overload Generators Config. This config is synced from server to client.").push("overloadgenerators");

        cpuUsageGeneratorStorage = CachedFloatingLongValue.define(this, builder, "CPU Usage Generators' base energy storage.", "cpuUsageGeneratorStorage", FloatingLong.createConst(8_000_000));
        cpuUsageGeneratorGeneration = CachedFloatingLongValue.define(this, builder, "CPU Usage Generators' base production rate.", "cpuUsageGeneratorGeneration", FloatingLong.createConst(800_000));
        cpuUsageGeneratorThreshold = CachedDoubleValue.wrap(this, builder.comment("CPU Usage Generators' threshold. When CPU Usage ratio exceeds this value, the generator starts producing energy.").defineInRange("cpuUsageGeneratorThreshold", 0.5, 0.0, 0.99));

        builder.pop();
        configSpec = builder.build();
    }

    @Override
    public String getFileName() {
        return "overloadgenerators";
    }

    @Override
    public ForgeConfigSpec getConfigSpec() {
        return configSpec;
    }

    @Override
    public Type getConfigType() {
        return Type.SERVER;
    }

    @Override
    public boolean addToContainer() {
        return false;
    }
}