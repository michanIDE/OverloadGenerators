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
import mekanism.common.inventory.container.sync.SyncableInt;
import mekanism.common.inventory.container.sync.SyncableLong;
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

public class BlockEntityTickTimeGenerator extends BlockEntityOverGen {
        
    protected int numberOfCores = 0;
    protected int numberOfCoresLastTick = 0;
    protected Long coreMultiplier = 1L;
    protected boolean isSafeMode = false;

    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getCoreItem", docPlaceholder = "core item slot")
    protected BasicInventorySlot coreSlot;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getEnergyItem", docPlaceholder = "energy item slot")
    protected EnergyInventorySlot energySlot;
    protected static final Predicate<@NotNull ItemStack> coreSlotValidator = stack -> stack.getItem() instanceof ItemCore;

    protected FloatingLong baseGeneration = FloatingLong.ZERO;
    protected Long tickTimeThreshold = 0L;
    private Long tickTime = 0L;
    private Long tickTimeExponent = 1L;

    public BlockEntityTickTimeGenerator(BlockPos pos, BlockState state) {
        this(OverGenBlocks.TICK_TIME_GENERATOR, pos, state, FloatingLong.MAX_VALUE);
    }

    protected BlockEntityTickTimeGenerator(IBlockProvider blockProvider, BlockPos pos, BlockState state, @Nonnull FloatingLong output) {
        super(blockProvider, pos, state, output);
        isSafeMode = OverGenConfig.config.isSafeMode.get();
        baseGeneration = OverGenConfig.config.tickTimeGeneratorGeneration.get();
        tickTimeThreshold = OverGenConfig.config.tickTimeGeneratorThreshold.get();
        baseEnergyStorage = OverGenConfig.config.tickTimeGeneratorStorage.get();
        tickTimeExponent = OverGenConfig.config.tickTimeGeneratorExponent.get();
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
        tickTime = GlobalTickHandler.getCachedTickTime();
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

    protected void updateCores(){
        // Multiplied by 1L to cast to long
        coreMultiplier = OverGenMath.pow(2L, numberOfCores * 1L);
        FloatingLong maxEnergyStorage = baseEnergyStorage.multiply(coreMultiplier);
        getEnergyContainer().setMaxEnergy(maxEnergyStorage);
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

    protected FloatingLong calcProduction() {
        if (level == null) {
            return FloatingLong.ZERO;
        }
        if (tickTime < tickTimeThreshold) {
            return FloatingLong.ZERO;
        }
        Long lag_ms = (tickTime - tickTimeThreshold) / 1_000_000L;
        Long multiplier = OverGenMath.pow(lag_ms, tickTimeExponent);
        return lag_ms > 0 ? baseGeneration.multiply(multiplier) : FloatingLong.ZERO;
    }

    @ComputerMethod
    public int getNumberOfCores() {
        return numberOfCores;
    }

    @ComputerMethod
    public Long getTickTime() {
        return tickTime;
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
        container.track(SyncableLong.create(this::getTickTime, value -> tickTime = value));
        container.track(SyncableInt.create(this::getNumberOfCores, value -> numberOfCores = value));
    }
}