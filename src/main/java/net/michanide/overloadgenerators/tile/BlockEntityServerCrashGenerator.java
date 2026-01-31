package net.michanide.overloadgenerators.tile;

import java.util.function.Predicate;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;

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
import mekanism.common.inventory.container.sync.SyncableLong;
import mekanism.common.inventory.slot.BasicInventorySlot;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.NBTUtils;
import net.michanide.overloadgenerators.config.OverGenConfig;
import net.michanide.overloadgenerators.handlers.ServerLifecycleHandler;
import net.michanide.overloadgenerators.init.OverGenBlocks;
import net.michanide.overloadgenerators.item.ItemCore;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class BlockEntityServerCrashGenerator extends BlockEntityOverGen {

    public static final String TILE_CRASH_COUNT_KEY = "tileCrashCount";

    protected int numberOfCores = 0;
    protected int numberOfCoresLastTick = 0;

    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getCoreItem", docPlaceholder = "core item slot")
    protected BasicInventorySlot coreSlot;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getEnergyItem", docPlaceholder = "energy item slot")
    protected EnergyInventorySlot energySlot;
    protected static final Predicate<@NotNull ItemStack> coreSlotValidator = stack -> stack.getItem() instanceof ItemCore;

    protected FloatingLong generationPerCrash = FloatingLong.ZERO;

    protected long tileCrashCount = 0L;
    protected long globalCrashCount = ServerLifecycleHandler.NOT_READY;

    public BlockEntityServerCrashGenerator(BlockPos pos, BlockState state) {
        this(OverGenBlocks.SERVER_CRASH_GENERATOR, pos, state, OverGenConfig.config.serverCrashGeneratorGeneration.get().multiply(2));
    }

    protected BlockEntityServerCrashGenerator(IBlockProvider blockProvider, BlockPos pos, BlockState state, @Nonnull FloatingLong output) {
        super(blockProvider, pos, state, output);
        generationPerCrash = OverGenConfig.config.serverCrashGeneratorGeneration.get();
        baseEnergyStorage = OverGenConfig.config.serverCrashGeneratorStorage.get();
        tileCrashCount = ServerLifecycleHandler.getTotalCrashCount();
        globalCrashCount = ServerLifecycleHandler.getTotalCrashCount();
    }

    @Nonnull
    @Override
    protected IInventorySlotHolder getInitialInventory(IContentsListener listener) {
        InventorySlotHelper builder = InventorySlotHelper.forSide(this::getDirection);
        builder.addSlot(coreSlot = BasicInventorySlot.at(coreSlotValidator, listener, 17, 35));
        builder.addSlot(energySlot = EnergyInventorySlot.drain(getEnergyContainer(), listener, 143, 35));
        return builder.build();
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        Long cachedProduction = 0L;
        energySlot.drainContainer();

        if(globalCrashCount == ServerLifecycleHandler.NOT_READY){
            globalCrashCount = ServerLifecycleHandler.getTotalCrashCount();
        }

        numberOfCoresLastTick = numberOfCores;
        numberOfCores = coreSlot.getCount();
        if(numberOfCores != numberOfCoresLastTick){
            processCores();
        }
        
        cachedProduction = process(generationPerCrash);
        lastProductionAmount = FloatingLong.create(cachedProduction);
    }

    protected void processCores(){
        if(numberOfCores == 64){
            if(OverGenConfig.config.isDebugMode.get()){
                coreSlot.getStack().setCount(63);
                throw new RuntimeException("Debug Mode: Simulated server crash due to overloaded core slot in Server Crash Generator at " + this.worldPosition);
            }
        }
    }

    protected Long process(FloatingLong production) {
        if (globalCrashCount == ServerLifecycleHandler.NOT_READY) {
            setActive(false);
            return 0L;
        }

        // If a crash was detected since last check, produce energy
        // tileCrashCount : Placed time or last crash check time
        if (MekanismUtils.canFunction(this) && tileCrashCount < globalCrashCount && globalCrashCount != ServerLifecycleHandler.NOT_READY && !getEnergyContainer().getNeeded().isZero()) {
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

    @ComputerMethod
    public boolean wasLastShutdownClean() {
        return !(ServerLifecycleHandler.getCrashDetected());
    }

    @ComputerMethod
    public long getTotalCrashCount() {
        return globalCrashCount;
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
        container.track(SyncableLong.create(this::getTileCrashCount, value -> tileCrashCount = value));
        container.track(SyncableLong.create(this::getTotalCrashCount, value -> globalCrashCount = value));
    }
}