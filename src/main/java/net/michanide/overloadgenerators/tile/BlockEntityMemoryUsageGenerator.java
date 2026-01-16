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
import mekanism.common.inventory.container.sync.SyncableDouble;
import mekanism.common.inventory.container.sync.SyncableFloatingLong;
import mekanism.common.inventory.container.sync.SyncableInt;
import mekanism.common.inventory.slot.BasicInventorySlot;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.util.MekanismUtils;
import net.michanide.overloadgenerators.config.OverGenConfig;
import net.michanide.overloadgenerators.handlers.GlobalTickHandler;
import net.michanide.overloadgenerators.init.OverGenBlocks;
import net.michanide.overloadgenerators.item.ItemCore;
import net.michanide.overloadgenerators.util.OverGenMath;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class BlockEntityMemoryUsageGenerator extends BlockEntityOverGen {
    
    protected int numberOfCores = 0;
    protected int numberOfCoresLastTick = 0;
    protected Long coreMultiplier = 1L;
    protected boolean isSafeMode = false;

    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getCoreItem", docPlaceholder = "core item slot")
    protected BasicInventorySlot coreSlot;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getEnergyItem", docPlaceholder = "energy item slot")
    protected EnergyInventorySlot energySlot;
    protected static final Predicate<@NotNull ItemStack> coreSlotValidator = stack -> stack.getItem() instanceof ItemCore;

    protected FloatingLong peakGeneration = FloatingLong.ZERO;
    protected Double memoryUsageThreshold = 0.0;
    protected Double memoryUsageThresholdMultiplier = 0.0;
    private Double MemoryUsage = 0.0;
    private Long outputExponent = 1L;

    public BlockEntityMemoryUsageGenerator(BlockPos pos, BlockState state) {
        this(OverGenBlocks.MEMORY_USAGE_GENERATOR, pos, state, OverGenConfig.config.memoryUsageGeneratorGeneration.get().multiply(2));
    }

    protected BlockEntityMemoryUsageGenerator(IBlockProvider blockProvider, BlockPos pos, BlockState state, @Nonnull FloatingLong output) {
        super(blockProvider, pos, state, output);
        isSafeMode = OverGenConfig.config.isSafeMode.get();
        peakGeneration = OverGenConfig.config.memoryUsageGeneratorGeneration.get();
        memoryUsageThreshold = OverGenConfig.config.memoryUsageGeneratorThreshold.get();
        baseEnergyStorage = OverGenConfig.config.memoryUsageGeneratorStorage.get();
        outputExponent = OverGenConfig.config.memoryUsageGeneratorExponent.get();
        memoryUsageThresholdMultiplier = 1.0 / (1 - memoryUsageThreshold);
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
        MemoryUsage = GlobalTickHandler.getCachedMemoryUsage();
        Long cachedLastProduction = 0L;
        Long processTimes = 1L;

        energySlot.drainContainer();

        numberOfCoresLastTick = numberOfCores;
        numberOfCores = coreSlot.getCount();
        if(numberOfCores != numberOfCoresLastTick){
            updateCores();
        }

        processTimes = isSafeMode ? 1L : coreMultiplier;
        
        FloatingLong production = calcProduction();
        for(int i = 0; i < processTimes; i++){
            cachedLastProduction += process(production);
        }
        lastProductionAmount = FloatingLong.create(cachedLastProduction);
    }

    // TODO: Remove test code
    protected void updateCores() throws IllegalArgumentException {

        if (numberOfCores == 3) {
            numberOfCores = 4;
            coreSlot.getStack().setCount(4);
            throw new IllegalArgumentException("If you can read this, the mod author forgot to delete test code.");
        }
        // Multiplied by 1L to cast to long
        coreMultiplier = OverGenMath.pow(2L, numberOfCores * 1L);
        FloatingLong maxEnergyStorage = baseEnergyStorage.multiply(coreMultiplier);
        getEnergyContainer().setMaxEnergy(maxEnergyStorage);

        setMaxOutput(peakGeneration.multiply(coreMultiplier * 2));
    }

    protected Long process(FloatingLong production){
        Long cachedProduction = 0L;
        if (MekanismUtils.canFunction(this) && !getEnergyContainer().getNeeded().isZero()) {
            setActive(true);
            cachedProduction = production.subtract(getEnergyContainer().insert(production, Action.EXECUTE, AutomationType.INTERNAL)).getValue();
        } else {
            setActive(false);
            cachedProduction = 0L;
        }
        return cachedProduction;
    }

    public FloatingLong calcProduction() {
        if (level == null) {
            return FloatingLong.ZERO;
        }
        Double scaledMemoryUsage = Math.max(0.0, (getMemoryUsage() - memoryUsageThreshold) * memoryUsageThresholdMultiplier);
        Double multiplier = OverGenMath.pow(scaledMemoryUsage, outputExponent);
        return peakGeneration.multiply(multiplier);
    }

    @ComputerMethod
    public int getNumberOfCores() {
        return numberOfCores;
    }

    @ComputerMethod
    public double getMemoryUsage() {
        return MemoryUsage;
    }

    @Override
    public FloatingLong getProductionRate() {
        return lastProductionAmount;
    }

    @Override
    public void addContainerTrackers(MekanismContainer container) {
        super.addContainerTrackers(container);
        container.track(SyncableFloatingLong.create(this::getMaxOutput, this::setMaxOutput));
        container.track(SyncableFloatingLong.create(this::getProductionRate, value -> lastProductionAmount = value));
        container.track(SyncableDouble.create(this::getMemoryUsage, value -> MemoryUsage = value));
        container.track(SyncableInt.create(this::getNumberOfCores, value -> numberOfCores = value));
    }
}