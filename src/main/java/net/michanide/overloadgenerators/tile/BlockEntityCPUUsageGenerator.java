package net.michanide.overloadgenerators.tile;

import javax.annotation.Nonnull;
import mekanism.api.math.FloatingLong;
import mekanism.api.providers.IBlockProvider;
import mekanism.common.integration.computer.annotation.ComputerMethod;
import mekanism.common.inventory.container.MekanismContainer;
import mekanism.common.inventory.container.sync.SyncableDouble;
import mekanism.common.inventory.container.sync.SyncableFloatingLong;
import mekanism.common.inventory.container.sync.SyncableInt;
import net.michanide.overloadgenerators.config.OverGenConfig;
import net.michanide.overloadgenerators.init.OverGenBlocks;
import net.michanide.overloadgenerators.util.GlobalTickHandler;
import net.michanide.overloadgenerators.util.OverGenMath;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class BlockEntityCPUUsageGenerator extends BlockEntityOverGen {

    protected FloatingLong peakGeneration = FloatingLong.ZERO;
    protected Double cpuUsageThreshold = 0.0;
    protected Double cpuUsageThresholdMultiplier = 0.0;
    private Double CPUUsage = 0.0;
    private Long outputExponent = 1L;

    public BlockEntityCPUUsageGenerator(BlockPos pos, BlockState state) {
        this(OverGenBlocks.CPU_USAGE_GENERATOR, pos, state, OverGenConfig.config.cpuUsageGeneratorGeneration.get().multiply(2));
    }

    protected BlockEntityCPUUsageGenerator(IBlockProvider blockProvider, BlockPos pos, BlockState state, @Nonnull FloatingLong output) {
        super(blockProvider, pos, state, output);
        peakGeneration = OverGenConfig.config.cpuUsageGeneratorGeneration.get();
        cpuUsageThreshold = OverGenConfig.config.cpuUsageGeneratorThreshold.get();
        baseEnergyStorage = OverGenConfig.config.cpuUsageGeneratorStorage.get();
        outputExponent = OverGenConfig.config.cpuUsageGeneratorExponent.get();
        cpuUsageThresholdMultiplier = 1.0 / (1 - cpuUsageThreshold);
    }

    @Override
    protected void onUpdateServer() {
        CPUUsage = GlobalTickHandler.getCachedCPUUsage();
        super.onUpdateServer();
    }

    @Override
    protected void updateCores(){
        super.updateCores();
        setMaxOutput(peakGeneration.multiply(coreMultiplier * 2));
    }

    @Override
    public FloatingLong getProduction() {
        if (level == null) {
            return FloatingLong.ZERO;
        }
        Double scaledCpuUsage = Math.max(0.0, (getCPUUsage() - cpuUsageThreshold) * cpuUsageThresholdMultiplier);
        Double multiplier = OverGenMath.pow(scaledCpuUsage, outputExponent);
        return peakGeneration.multiply(multiplier);
    }

    @ComputerMethod
    public double getCPUUsage() {
        return CPUUsage;
    }

    @Override
    public void addContainerTrackers(MekanismContainer container) {
        super.addContainerTrackers(container);
        container.track(SyncableFloatingLong.create(this::getMaxOutput, this::setMaxOutput));
        container.track(SyncableFloatingLong.create(this::getLastProductionAmount, value -> lastProductionAmount = value));
        container.track(SyncableDouble.create(this::getCPUUsage, value -> CPUUsage = value));
        container.track(SyncableInt.create(this::getNumberOfCores, value -> numberOfCores = value));
    }
}