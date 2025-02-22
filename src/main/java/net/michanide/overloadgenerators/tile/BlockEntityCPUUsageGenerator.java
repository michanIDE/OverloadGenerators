package net.michanide.overloadgenerators.tile;

import javax.annotation.Nonnull;

import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
import mekanism.api.RelativeSide;
import mekanism.api.math.FloatingLong;
import mekanism.api.providers.IBlockProvider;
import mekanism.common.capabilities.holder.slot.IInventorySlotHolder;
import mekanism.common.capabilities.holder.slot.InventorySlotHelper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper;
import mekanism.common.integration.computer.annotation.ComputerMethod;
import mekanism.common.integration.computer.annotation.WrappingComputerMethod;
import mekanism.common.inventory.container.MekanismContainer;
import mekanism.common.inventory.container.sync.SyncableFloatingLong;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.util.MekanismUtils;
import mekanism.generators.common.tile.TileEntityGenerator;
import net.michanide.overloadgenerators.config.OverGenConfig;
import net.michanide.overloadgenerators.init.OverGenBlocks;
import net.michanide.overloadgenerators.util.GlobalTickHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class BlockEntityCPUUsageGenerator extends TileEntityGenerator {

    protected FloatingLong peakOutput = FloatingLong.ZERO;
    protected Double cpuUsageThreshold = 0.0;
    private FloatingLong lastProductionAmount = FloatingLong.ZERO;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getEnergyItem")
    private EnergyInventorySlot energySlot;

    public BlockEntityCPUUsageGenerator(BlockPos pos, BlockState state) {
        this(OverGenBlocks.CPU_USAGE_GENERATOR, pos, state, FloatingLong.create(Long.MAX_VALUE));
    }

    protected BlockEntityCPUUsageGenerator(IBlockProvider blockProvider, BlockPos pos, BlockState state, @Nonnull FloatingLong output) {
        super(blockProvider, pos, state, output);
        peakOutput = OverGenConfig.config.cpuUsageGeneratorGeneration.get();
        cpuUsageThreshold = OverGenConfig.config.cpuUsageGeneratorThreshold.get();
    }

    @Nonnull
    @Override
    protected IInventorySlotHolder getInitialInventory(IContentsListener listener) {
        InventorySlotHelper builder = InventorySlotHelper.forSide(this::getDirection);
        builder.addSlot(energySlot = EnergyInventorySlot.drain(getEnergyContainer(), listener, 143, 35));
        return builder.build();
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();

        // System.out.println("onUpdateServer");
        energySlot.drainContainer();
        if (MekanismUtils.canFunction(this) && !getEnergyContainer().getNeeded().isZero()) {
            setActive(true);
            FloatingLong production = getProduction();
            lastProductionAmount = production.subtract(getEnergyContainer().insert(production, Action.EXECUTE, AutomationType.INTERNAL));
        } else {
            setActive(false);
            lastProductionAmount = FloatingLong.ZERO;
        }
    }

    public FloatingLong getProduction() {
        if (level == null) {
            return FloatingLong.ZERO;
        }
        double scaledCpuUsage = Math.max(0.0, (getCpuUsage() - cpuUsageThreshold) * 2);
        double multiplier = scaledCpuUsage * scaledCpuUsage;
        return peakOutput.multiply(multiplier);
    }

    @Override
    protected RelativeSide[] getEnergySides() {
        return new RelativeSide[]{RelativeSide.BACK, RelativeSide.FRONT, RelativeSide.TOP, RelativeSide.BOTTOM, RelativeSide.RIGHT, RelativeSide.LEFT};
    }

    @Override
    public FloatingLong getMaxOutput() {
        return peakOutput;
    }

    @ComputerMethod(nameOverride = "getProductionRate")
    public FloatingLong getLastProductionAmount() {
        return lastProductionAmount;
    }

    public double getCpuUsage() {
        return GlobalTickHandler.getCachedCPUUsage();
    }

    @Override
    public void addContainerTrackers(MekanismContainer container) {
        super.addContainerTrackers(container);
        container.track(SyncableFloatingLong.create(this::getMaxOutput, value -> peakOutput = value));
        container.track(SyncableFloatingLong.create(this::getLastProductionAmount, value -> lastProductionAmount = value));
    }
}