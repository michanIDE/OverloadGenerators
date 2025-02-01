package net.michanide.overloadgenerators.config;

import mekanism.api.math.FloatingLong;
import mekanism.common.config.BaseMekanismConfig;
import mekanism.common.config.value.CachedFloatingLongValue;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig.Type;


public class ConfigDetail extends BaseMekanismConfig {

    private final ForgeConfigSpec configSpec;

    public final CachedFloatingLongValue overheatGeneratorStorage;
    public final CachedFloatingLongValue overheatGeneratorGeneration;

    ConfigDetail() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.comment("Overload Generators Energy Storage Config. This config is synced from server to client.").push("overloadgenerators");

        overheatGeneratorStorage = CachedFloatingLongValue.define(this, builder, "Base energy storage.", "overheatGeneratorStorage", FloatingLong.createConst(4_000_000));
        overheatGeneratorGeneration = CachedFloatingLongValue.define(this, builder, "Base production rate.", "overheatGeneratorGeneration", FloatingLong.createConst(1_000));

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