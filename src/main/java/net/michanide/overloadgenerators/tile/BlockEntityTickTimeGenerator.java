package net.michanide.overloadgenerators.tile;

import javax.annotation.Nonnull;
import java.util.function.Predicate;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
import mekanism.api.RelativeSide;
import mekanism.api.annotations.NonNull;
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
import net.michanide.overloadgenerators.init.OverGenBlocks;
import net.michanide.overloadgenerators.item.ItemCore;
import net.michanide.overloadgenerators.util.GlobalTickHandler;
import net.michanide.overloadgenerators.util.OverGenMath;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class BlockEntityTickTimeGenerator extends BlockEntityOverGen {

    protected FloatingLong baseGeneration = FloatingLong.ZERO;
    private FloatingLong lastProductionAmount = FloatingLong.ZERO;
    private FloatingLong baseEnergyStorage = FloatingLong.ZERO;
    protected Double tickTimeThresholdMultiplier = 0.0;
    protected Long tickTimeThreshold = 0L;
    private Long tickTime = 0L;
    private int numberOfCores = 0;
    private int numberOfCoresLastTick = 0;
    private Long processTimes = 1L;
    private boolean isSafeMode = false;

    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getCoreItem")
    private BasicInventorySlot coreSlot;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getEnergyItem")
    private EnergyInventorySlot energySlot;

    private static final Predicate<@NonNull ItemStack> coreSlotValidator = stack -> stack.getItem() instanceof ItemCore;

    public BlockEntityTickTimeGenerator(BlockPos pos, BlockState state) {
        this(OverGenBlocks.CPU_USAGE_GENERATOR, pos, state, OverGenConfig.config.tickTimeGeneratorGeneration.get().multiply(2));
    }

    protected BlockEntityTickTimeGenerator(IBlockProvider blockProvider, BlockPos pos, BlockState state, @Nonnull FloatingLong output) {
        super(blockProvider, pos, state, output);
        baseGeneration = OverGenConfig.config.tickTimeGeneratorGeneration.get();
        tickTimeThreshold = OverGenConfig.config.tickTimeGeneratorThreshold.get();
        baseEnergyStorage = OverGenConfig.config.tickTimeGeneratorStorage.get();
        isSafeMode = OverGenConfig.config.isSafeMode.get();
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
        System.out.println("Tick");
        // System.out.println("onUpdateServer");
        tickTime = GlobalTickHandler.getCachedTickTime();
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
        setMaxOutput(baseGeneration.multiply(multiplier * 2));
        processTimes = multiplier;
    }

    public FloatingLong getProduction() {
        if (level == null) {
            return FloatingLong.ZERO;
        }
        return FloatingLong.create(10L).multiply(tickTime);
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
    public int getNumberOfCores() {
        return numberOfCores;
    }

    @Override
    public void addContainerTrackers(MekanismContainer container) {
        super.addContainerTrackers(container);
        container.track(SyncableFloatingLong.create(this::getMaxOutput, this::setMaxOutput));
        container.track(SyncableFloatingLong.create(this::getLastProductionAmount, value -> lastProductionAmount = value));
        container.track(SyncableInt.create(this::getNumberOfCores, value -> numberOfCores = value));
    }
}