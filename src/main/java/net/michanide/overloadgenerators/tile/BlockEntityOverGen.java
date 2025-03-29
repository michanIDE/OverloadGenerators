package net.michanide.overloadgenerators.tile;

import java.util.EnumSet;
import java.util.Set;

import javax.annotation.Nonnull;

import mekanism.api.IContentsListener;
import mekanism.api.RelativeSide;
import mekanism.api.math.FloatingLong;
import mekanism.api.providers.IBlockProvider;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import mekanism.common.capabilities.holder.energy.EnergyContainerHelper;
import mekanism.common.capabilities.holder.energy.IEnergyContainerHolder;
import mekanism.common.capabilities.resolver.BasicCapabilityResolver;
import mekanism.common.integration.computer.annotation.ComputerMethod;
import mekanism.common.inventory.container.MekanismContainer;
import mekanism.common.inventory.container.sync.SyncableFloatingLong;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.util.CableUtils;
import mekanism.common.util.MekanismUtils;
import net.michanide.overloadgenerators.capability.OverGenEnergyContainer;
import net.michanide.overloadgenerators.config.OverGenConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class BlockEntityOverGen extends TileEntityMekanism {

    /**
     * Output per tick this generator can transfer.
     */
    public FloatingLong output;
    private OverGenEnergyContainer energyContainer;
    protected boolean isSafeMode = false;

    /**
     * Generator -- a block that produces energy. It has a certain amount of fuel it can store as well as an output rate.
     */
    public BlockEntityOverGen(IBlockProvider blockProvider, BlockPos pos, BlockState state, @Nonnull FloatingLong out) {
        super(blockProvider, pos, state);
        output = out;
        isSafeMode = OverGenConfig.config.isSafeMode.get();
        addCapabilityResolver(BasicCapabilityResolver.constant(Capabilities.CONFIG_CARD_CAPABILITY, this));
    }

    protected RelativeSide[] getEnergySides() {
        return new RelativeSide[]{RelativeSide.FRONT};
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
            //TODO: Cache the directions or maybe even make some generators have a side config/ejector component and move this to the ejector component?
            Set<Direction> emitDirections = EnumSet.noneOf(Direction.class);
            Direction direction = getDirection();
            for (RelativeSide energySide : getEnergySides()) {
                emitDirections.add(energySide.getDirection(direction));
            }
            CableUtils.emit(emitDirections, energyContainer, this, getMaxOutput());
        }
    }

    @ComputerMethod
    public FloatingLong getMaxOutput() {
        return output;
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