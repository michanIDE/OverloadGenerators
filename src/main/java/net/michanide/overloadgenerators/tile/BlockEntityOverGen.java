package net.michanide.overloadgenerators.tile;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.Predicate;

import javax.annotation.Nonnull;

import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
import mekanism.api.RelativeSide;
import mekanism.api.annotations.NonNull;
import mekanism.api.math.FloatingLong;
import mekanism.api.providers.IBlockProvider;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import mekanism.common.capabilities.holder.energy.EnergyContainerHelper;
import mekanism.common.capabilities.holder.energy.IEnergyContainerHolder;
import mekanism.common.capabilities.holder.slot.IInventorySlotHolder;
import mekanism.common.capabilities.holder.slot.InventorySlotHelper;
import mekanism.common.capabilities.resolver.BasicCapabilityResolver;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper;
import mekanism.common.integration.computer.annotation.ComputerMethod;
import mekanism.common.integration.computer.annotation.WrappingComputerMethod;
import mekanism.common.inventory.container.MekanismContainer;
import mekanism.common.inventory.container.sync.SyncableFloatingLong;
import mekanism.common.inventory.slot.BasicInventorySlot;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.util.CableUtils;
import mekanism.common.util.MekanismUtils;
import net.michanide.overloadgenerators.capability.OverGenEnergyContainer;
import net.michanide.overloadgenerators.config.OverGenConfig;
import net.michanide.overloadgenerators.item.ItemCore;
import net.michanide.overloadgenerators.util.OverGenMath;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class BlockEntityOverGen extends TileEntityMekanism {

    /**
     * Output per tick this generator can transfer.
     */
    public FloatingLong output;
    private OverGenEnergyContainer energyContainer;

    
    protected FloatingLong baseEnergyStorage = FloatingLong.ZERO;
    protected FloatingLong lastProductionAmount = FloatingLong.ZERO;
    protected int numberOfCores = 0;
    protected int numberOfCoresLastTick = 0;
    protected Long coreMultiplier = 1L;
    protected boolean isSafeMode = false;

    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getCoreItem")
    protected BasicInventorySlot coreSlot;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getEnergyItem")
    protected EnergyInventorySlot energySlot;

    protected static final Predicate<@NonNull ItemStack> coreSlotValidator = stack -> stack.getItem() instanceof ItemCore;

    public BlockEntityOverGen(IBlockProvider blockProvider, BlockPos pos, BlockState state, @Nonnull FloatingLong out) {
        super(blockProvider, pos, state);
        output = out;
        isSafeMode = OverGenConfig.config.isSafeMode.get();
        addCapabilityResolver(BasicCapabilityResolver.constant(Capabilities.CONFIG_CARD_CAPABILITY, this));
    }

    @Nonnull
    @Override
    protected IInventorySlotHolder getInitialInventory(IContentsListener listener) {
        InventorySlotHelper builder = InventorySlotHelper.forSide(this::getDirection);
        builder.addSlot(coreSlot = BasicInventorySlot.at(coreSlotValidator, listener, 17, 35));
        builder.addSlot(energySlot = EnergyInventorySlot.drain(getEnergyContainer(), listener, 143, 35));
        return builder.build();
    }

    @Nonnull
    @Override
    protected IEnergyContainerHolder getInitialEnergyContainers(IContentsListener listener) {
        EnergyContainerHelper builder = EnergyContainerHelper.forSide(this::getDirection);
        builder.addContainer(energyContainer = OverGenEnergyContainer.output(MachineEnergyContainer.validateBlock(this).getStorage(), listener), getEnergySides());
        return builder.build();
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        if (MekanismUtils.canFunction(this)) {
            Set<Direction> emitDirections = EnumSet.noneOf(Direction.class);
            Direction direction = getDirection();
            for (RelativeSide energySide : getEnergySides()) {
                emitDirections.add(energySide.getDirection(direction));
            }
            CableUtils.emit(emitDirections, energyContainer, this, getMaxOutput());
        }

        Long cachedLastProduction = 0L;
        Long processTimes = 1L;
        energySlot.drainContainer();
        numberOfCoresLastTick = numberOfCores;
        numberOfCores = coreSlot.getCount();
        if(numberOfCores != numberOfCoresLastTick){
            updateCores();
        }
        processTimes = isSafeMode ? 1L : coreMultiplier;
        for(int i = 0; i < processTimes; i++){
            Long cachedProduction = process();
            cachedLastProduction += cachedProduction;
        }
        lastProductionAmount = FloatingLong.create(cachedLastProduction);
    }

    protected Long process(){
        Long cachedProduction = 0L;
        if (MekanismUtils.canFunction(this) && !getEnergyContainer().getNeeded().isZero()) {
            setActive(true);
            FloatingLong production = getProduction();
            cachedProduction = production.subtract(getEnergyContainer().insert(production, Action.EXECUTE, AutomationType.INTERNAL)).getValue();
        } else {
            setActive(false);
            cachedProduction = 0L;
        }
        return cachedProduction;
    }

    protected void updateCores(){
        // Multiplied by 1L to cast to long
        coreMultiplier = OverGenMath.pow(2L, numberOfCores * 1L);
        FloatingLong maxEnergyStorage = baseEnergyStorage.multiply(coreMultiplier);
        getEnergyContainer().setMaxEnergy(maxEnergyStorage);
    }

    protected FloatingLong getProduction() {
        return FloatingLong.ZERO;
    }

    protected RelativeSide[] getEnergySides() {
        return new RelativeSide[]{RelativeSide.BACK, RelativeSide.FRONT, RelativeSide.TOP, RelativeSide.BOTTOM, RelativeSide.RIGHT, RelativeSide.LEFT};
    }

    @ComputerMethod
    public FloatingLong getMaxOutput() {
        return output;
    }

    @ComputerMethod(nameOverride = "getProductionRate")
    public FloatingLong getLastProductionAmount() {
        return lastProductionAmount;
    }

    @ComputerMethod
    public int getNumberOfCores() {
        return numberOfCores;
    }

    public void setMaxOutput(FloatingLong out) {
        output = out;
    }

    public OverGenEnergyContainer getEnergyContainer() {
        return energyContainer;
    }

    @Override
    public void addContainerTrackers(MekanismContainer container) {
        super.addContainerTrackers(container);
        container.track(SyncableFloatingLong.create(energyContainer::getMaxEnergy, energyContainer::setMaxEnergy));
    }
}