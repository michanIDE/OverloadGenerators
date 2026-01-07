package net.michanide.overloadgenerators.tile;

import javax.annotation.Nonnull;

import mekanism.api.math.FloatingLong;
import mekanism.api.providers.IBlockProvider;
import mekanism.common.integration.computer.annotation.ComputerMethod;
import mekanism.common.inventory.container.MekanismContainer;
import mekanism.common.inventory.container.sync.SyncableFloatingLong;
import mekanism.common.inventory.container.sync.SyncableInt;
import mekanism.common.inventory.container.sync.SyncableLong;
import net.michanide.overloadgenerators.config.OverGenConfig;
import net.michanide.overloadgenerators.init.OverGenBlocks;
import net.michanide.overloadgenerators.util.GlobalTickHandler;
import net.michanide.overloadgenerators.util.OverGenMath;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class BlockEntityTickTimeGenerator extends BlockEntityOverGen {

    protected FloatingLong baseGeneration = FloatingLong.ZERO;
    protected Long tickTimeThreshold = 0L;
    private Long tickTime = 0L;
    private Long tickTimeExponent = 1L;

    public BlockEntityTickTimeGenerator(BlockPos pos, BlockState state) {
        this(OverGenBlocks.TICK_TIME_GENERATOR, pos, state, FloatingLong.MAX_VALUE);
    }

    protected BlockEntityTickTimeGenerator(IBlockProvider blockProvider, BlockPos pos, BlockState state, @Nonnull FloatingLong output) {
        super(blockProvider, pos, state, output);
        baseGeneration = OverGenConfig.config.tickTimeGeneratorGeneration.get();
        tickTimeThreshold = OverGenConfig.config.tickTimeGeneratorThreshold.get();
        baseEnergyStorage = OverGenConfig.config.tickTimeGeneratorStorage.get();
        tickTimeExponent = OverGenConfig.config.tickTimeGeneratorExponent.get();
    }

    @Override
    protected void onUpdateServer() {
        tickTime = GlobalTickHandler.getCachedTickTime();
        super.onUpdateServer();
    }

    @Override
    public FloatingLong getProduction() {
        if (level == null) {
            return FloatingLong.ZERO;
        }
        if (tickTime < tickTimeThreshold) {
            return FloatingLong.ZERO;
        }
        Long lag_ms = (tickTime - tickTimeThreshold) / 1_000_000L;
        return lag_ms > 0 ? baseGeneration.multiply(OverGenMath.pow(lag_ms, tickTimeExponent)) : FloatingLong.ZERO;
    }

    @ComputerMethod
    public Long getTickTime() {
        return tickTime;
    }

    @Override
    public void addContainerTrackers(MekanismContainer container) {
        super.addContainerTrackers(container);
        container.track(SyncableFloatingLong.create(this::getMaxOutput, this::setMaxOutput));
        container.track(SyncableFloatingLong.create(this::getLastProductionAmount, value -> lastProductionAmount = value));
        container.track(SyncableLong.create(this::getTickTime, value -> tickTime = value));
        container.track(SyncableInt.create(this::getNumberOfCores, value -> numberOfCores = value));
    }
}