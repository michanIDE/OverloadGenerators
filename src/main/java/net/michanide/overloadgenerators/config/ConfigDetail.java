package net.michanide.overloadgenerators.config;

import mekanism.api.math.FloatingLong;
import mekanism.common.config.BaseMekanismConfig;
import mekanism.common.config.value.CachedBooleanValue;
import mekanism.common.config.value.CachedDoubleValue;
import mekanism.common.config.value.CachedFloatingLongValue;
import mekanism.common.config.value.CachedLongValue;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig.Type;


public class ConfigDetail extends BaseMekanismConfig {

    private final ForgeConfigSpec configSpec;

    public final CachedBooleanValue isSafeMode;
    
    public final CachedFloatingLongValue cpuUsageGeneratorStorage;
    public final CachedFloatingLongValue cpuUsageGeneratorGeneration;
    public final CachedDoubleValue cpuUsageGeneratorThreshold;
    public final CachedLongValue cpuUsageGeneratorExponent;
    public final CachedBooleanValue cpuUsageGeneratorUseSystemInfo;

    public final CachedFloatingLongValue tickTimeGeneratorStorage;
    public final CachedFloatingLongValue tickTimeGeneratorGeneration;
    public final CachedLongValue tickTimeGeneratorThreshold;
    public final CachedLongValue tickTimeGeneratorExponent;
    
    public final CachedFloatingLongValue memoryUsageGeneratorStorage;
    public final CachedFloatingLongValue memoryUsageGeneratorGeneration;
    public final CachedDoubleValue memoryUsageGeneratorThreshold;
    public final CachedLongValue memoryUsageGeneratorExponent;
    public final CachedBooleanValue memoryUsageGeneratorUseSystemInfo;
    

    ConfigDetail() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.comment("Overload Generators Config. This config is synced from server to client.").push("overloadgenerators");

        isSafeMode = CachedBooleanValue.wrap(this, builder.comment("When this value is set to true, the number of processes is limited to 1. Turn this true if you installed too much core on a generator.").define("isSafeMode", false));

        // CPU Usage Generator
        cpuUsageGeneratorStorage = CachedFloatingLongValue.define(this, builder, "CPU Usage Generators' base energy storage.", "cpuUsageGeneratorStorage", FloatingLong.createConst(8_000_000));
        cpuUsageGeneratorGeneration = CachedFloatingLongValue.define(this, builder, "CPU Usage Generators' base production rate.", "cpuUsageGeneratorGeneration", FloatingLong.createConst(800_000));
        cpuUsageGeneratorThreshold = CachedDoubleValue.wrap(this, builder.comment("CPU Usage Generators' threshold. When CPU usage ratio exceeds this value, the generator starts producing energy.").defineInRange("cpuUsageGeneratorThreshold", 0.5, 0.0, 0.99));
        cpuUsageGeneratorExponent = CachedLongValue.wrap(this, builder.comment("Exponent used for CPU Usage Generator output; higher values mean more rapid changes.").defineInRange("cpuUsageGeneratorExponent", 2, 1, Long.MAX_VALUE));
        cpuUsageGeneratorUseSystemInfo = CachedBooleanValue.wrap(this, builder.comment("If true, this mod uses the value of real system's CPU usage. If false, it use the value of JVM process. (Turning this to false impacts performance)").define("cpuUsageGeneratorUseSystemInfo", true));

        // Tick Time Generator
        tickTimeGeneratorStorage = CachedFloatingLongValue.define(this, builder, "Tick Time Generators' base energy storage.", "tickTimeGeneratorStorage", FloatingLong.createConst(16_384_000_000L));
        tickTimeGeneratorGeneration = CachedFloatingLongValue.define(this, builder, "Tick Time Generators' base production rate.", "tickTimeGeneratorGeneration", FloatingLong.createConst(16_000));
        tickTimeGeneratorThreshold = CachedLongValue.wrap(this, builder.comment("Tick Time Generators' threshold (ns). When server tick time exceeds this value, the generator starts producing energy.").defineInRange("tickTimeGeneratorThreshold", 50_000_000, 0, Long.MAX_VALUE));
        tickTimeGeneratorExponent = CachedLongValue.wrap(this, builder.comment("Exponent used for Tick Time Generator output; higher values mean more rapid changes.").defineInRange("tickTimeGeneratorExponent", 3, 1, Long.MAX_VALUE));

        // Memory Usage Generator
        memoryUsageGeneratorStorage = CachedFloatingLongValue.define(this, builder, "Memory Usage Generators' base energy storage.", "memoryUsageGeneratorStorage", FloatingLong.createConst(8_000_000));
        memoryUsageGeneratorGeneration = CachedFloatingLongValue.define(this, builder, "Memory Usage Generators' base production rate.", "memoryUsageGeneratorGeneration", FloatingLong.createConst(800_000));
        memoryUsageGeneratorThreshold = CachedDoubleValue.wrap(this, builder.comment("Memory Usage Generators' threshold. When memory usage ratio exceeds this value, the generator starts producing energy.").defineInRange("memoryUsageGeneratorThreshold", 0.5, 0.0, 0.99));
        memoryUsageGeneratorExponent = CachedLongValue.wrap(this, builder.comment("Exponent used for Memory Usage Generator output; higher values mean more rapid changes.").defineInRange("memoryUsageGeneratorExponent", 1, 1, Long.MAX_VALUE));
        memoryUsageGeneratorUseSystemInfo = CachedBooleanValue.wrap(this, builder.comment("If true, this mod uses the value of real system's memory usage. If false, it use the value of JVM process.").define("memoryUsageGeneratorUseSystemInfo", false));

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