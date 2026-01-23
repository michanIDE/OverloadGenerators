package net.michanide.overloadgenerators.tile;

import javax.annotation.Nonnull;

import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
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
import mekanism.common.util.NBTUtils;
import net.michanide.overloadgenerators.config.OverGenConfig;
import net.michanide.overloadgenerators.handlers.ServerLifecycleHandler;
import net.michanide.overloadgenerators.init.OverGenBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public class BlockEntityServerCrashGenerator extends BlockEntityOverGen {

    public static final String TILE_CRASH_COUNT_KEY = "tileCrashCount";

    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getEnergyItem", docPlaceholder = "energy item slot")
    protected EnergyInventorySlot energySlot;

    protected FloatingLong generationPerCrash = FloatingLong.ZERO;

    protected long tileCrashCount = 0L;

    public BlockEntityServerCrashGenerator(BlockPos pos, BlockState state) {
        this(OverGenBlocks.SERVER_CRASH_GENERATOR, pos, state, OverGenConfig.config.serverCrashGeneratorGeneration.get().multiply(2));
    }

    protected BlockEntityServerCrashGenerator(IBlockProvider blockProvider, BlockPos pos, BlockState state, @Nonnull FloatingLong output) {
        super(blockProvider, pos, state, output);
        generationPerCrash = OverGenConfig.config.serverCrashGeneratorGeneration.get();
        baseEnergyStorage = OverGenConfig.config.serverCrashGeneratorStorage.get();
        tileCrashCount = ServerLifecycleHandler.getTotalCrashCount();
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
        Long cachedProduction = 0L;
        energySlot.drainContainer();
        
        cachedProduction = process(generationPerCrash);
        lastProductionAmount = FloatingLong.create(cachedProduction);
    }

    protected Long process(FloatingLong production) {
        long globalCrashCount = ServerLifecycleHandler.getTotalCrashCount();

        if (globalCrashCount == ServerLifecycleHandler.NOT_READY) {
            setActive(false);
            return 0L;
        }

        // If a crash was detected since last check, produce energy
        // tileCrashCount : Placed time or last crash check time
        if (MekanismUtils.canFunction(this) && tileCrashCount < globalCrashCount && ServerLifecycleHandler.getCrashDetected() && !getEnergyContainer().getNeeded().isZero()) {
            setActive(true);
            tileCrashCount++;
            return production.subtract(getEnergyContainer().insert(production, Action.EXECUTE, AutomationType.INTERNAL)).getValue();
        } else {
            setActive(false);
            return 0L;
        }
    }

    @Override
    protected void loadGeneralPersistentData(CompoundTag data) {
        super.loadGeneralPersistentData(data);
        NBTUtils.setLongIfPresent(data, TILE_CRASH_COUNT_KEY, value -> tileCrashCount = value);
    }

    @Override
    protected void addGeneralPersistentData(CompoundTag data) {
        super.addGeneralPersistentData(data);
        data.putLong(TILE_CRASH_COUNT_KEY, tileCrashCount);
    }

    @Override
    public FloatingLong getProductionRate() {
        return lastProductionAmount;
    }

    // Todo: Implement actual crash count tracking
    @ComputerMethod
    public boolean wasLastShutdownClean() {
        return true;
    }

    @ComputerMethod
    public long getTotalCrashCount() {
        return ServerLifecycleHandler.getTotalCrashCount();
    }

    @ComputerMethod
    public long getTileCrashCount() {
        return tileCrashCount;
    }

    @Override
    public void addContainerTrackers(MekanismContainer container) {
        super.addContainerTrackers(container);
        container.track(SyncableFloatingLong.create(this::getMaxOutput, this::setMaxOutput));
        container.track(SyncableFloatingLong.create(this::getProductionRate, value -> lastProductionAmount = value));
    }
}