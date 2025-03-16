package net.michanide.overloadgenerators.tile;

import javax.annotation.Nonnull;

import org.checkerframework.checker.units.qual.m;
import org.lwjgl.system.CallbackI.S;

import java.util.function.Predicate;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
import mekanism.api.RelativeSide;
import mekanism.api.annotations.NonNull;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.math.FloatingLong;
import mekanism.api.providers.IBlockProvider;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
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
import net.michanide.overloadgenerators.init.OverGenBlocks;
import net.michanide.overloadgenerators.item.ItemCore;
import net.michanide.overloadgenerators.util.GlobalTickHandler;
import net.michanide.overloadgenerators.util.OverGenMath;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class BlockEntityCPUUsageGenerator extends BlockEntityOverGen {

    protected FloatingLong peakGeneration = FloatingLong.ZERO;
    private FloatingLong lastProductionAmount = FloatingLong.ZERO;
    private FloatingLong baseEnergyStorage = FloatingLong.ZERO;
    protected Double cpuUsageThreshold = 0.0;
    protected Double cpuUsageThresholdMultiplier = 0.0;
    private Double CPUUsage = 0.0;
    private int numberOfCores = 0;
    private int numberOfCoresLastTick = 0;
    private Long processTimes = 1L;
    private boolean isSafeMode = false;

    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getCoreItem")
    private BasicInventorySlot coreSlot;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getEnergyItem")
    private EnergyInventorySlot energySlot;

    private static final Predicate<@NonNull ItemStack> coreSlotValidator = stack -> stack.getItem() instanceof ItemCore;

    public BlockEntityCPUUsageGenerator(BlockPos pos, BlockState state) {
        this(OverGenBlocks.CPU_USAGE_GENERATOR, pos, state, OverGenConfig.config.cpuUsageGeneratorGeneration.get().multiply(2));
    }

    protected BlockEntityCPUUsageGenerator(IBlockProvider blockProvider, BlockPos pos, BlockState state, @Nonnull FloatingLong output) {
        super(blockProvider, pos, state, output);
        peakGeneration = OverGenConfig.config.cpuUsageGeneratorGeneration.get();
        cpuUsageThreshold = OverGenConfig.config.cpuUsageGeneratorThreshold.get();
        baseEnergyStorage = OverGenConfig.config.cpuUsageGeneratorStorage.get();
        isSafeMode = OverGenConfig.config.isSafeMode.get();
        cpuUsageThresholdMultiplier = 1.0 / (1 - cpuUsageThreshold);
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

        // System.out.println("onUpdateServer");
        CPUUsage = GlobalTickHandler.getCachedCPUUsage();
        energySlot.drainContainer();
        numberOfCoresLastTick = numberOfCores;
        numberOfCores = coreSlot.getCount();
        if(numberOfCores != numberOfCoresLastTick){
            updateCores();
        }
        if(!isSafeMode){    
            for(int i = 0; i < processTimes; i++){
                process();
            }
        }
    }

    private void process(){
        if (MekanismUtils.canFunction(this) && !getEnergyContainer().getNeeded().isZero()) {
            setActive(true);
            FloatingLong production = getProduction();
            lastProductionAmount = production.subtract(getEnergyContainer().insert(production, Action.EXECUTE, AutomationType.INTERNAL));
        } else {
            setActive(false);
            lastProductionAmount = FloatingLong.ZERO;
        }
    }

    private void updateCores(){
        // Multiplied by 1L to cast to long
        Long multiplier = OverGenMath.pow(2L, numberOfCores * 1L);
        FloatingLong maxEnergyStorage = baseEnergyStorage.multiply(multiplier);
        getEnergyContainer().setMaxEnergy(maxEnergyStorage);
        setMaxOutput(peakGeneration.multiply(multiplier * 2));
        processTimes = multiplier;
    }

    public FloatingLong getProduction() {
        if (level == null) {
            return FloatingLong.ZERO;
        }
        double scaledCpuUsage = Math.max(0.0, (getCPUUsage() - cpuUsageThreshold) * cpuUsageThresholdMultiplier);
        double multiplier = scaledCpuUsage * scaledCpuUsage;
        return peakGeneration.multiply(multiplier);
    }

    @Override
    protected RelativeSide[] getEnergySides() {
        return new RelativeSide[]{RelativeSide.BACK, RelativeSide.FRONT, RelativeSide.TOP, RelativeSide.BOTTOM, RelativeSide.RIGHT, RelativeSide.LEFT};
    }

    @ComputerMethod(nameOverride = "getProductionRate")
    public FloatingLong getLastProductionAmount() {
        return lastProductionAmount;
    }

    @ComputerMethod
    public double getCPUUsage() {
        return CPUUsage;
    }

    @ComputerMethod
    public int getNumberOfCores() {
        return numberOfCores;
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