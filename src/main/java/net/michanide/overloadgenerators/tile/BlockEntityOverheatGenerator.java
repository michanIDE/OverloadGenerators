package net.michanide.overloadgenerators.tile;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
import mekanism.api.RelativeSide;
import mekanism.api.math.FloatingLong;
import mekanism.api.providers.IBlockProvider;
import mekanism.common.capabilities.holder.slot.IInventorySlotHolder;
import mekanism.common.capabilities.holder.slot.InventorySlotHelper;
import mekanism.common.inventory.container.MekanismContainer;
import mekanism.common.inventory.container.sync.SyncableBoolean;
import mekanism.common.inventory.container.sync.SyncableFloatingLong;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.WorldUtils;
import mekanism.generators.common.tile.TileEntityGenerator;
import net.michanide.overloadgenerators.config.OverGenConfig;
import net.michanide.overloadgenerators.init.OverGenBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biome.Precipitation;
import net.minecraft.world.level.block.state.BlockState;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.Sensors;

public class BlockEntityOverheatGenerator extends TileEntityGenerator {

    private boolean seesSun;
    protected FloatingLong peakOutput = FloatingLong.ZERO;
    private FloatingLong lastProductionAmount = FloatingLong.ZERO;
    private EnergyInventorySlot energySlot;
    @Nullable
    protected SolarCheck solarCheck;

    public BlockEntityOverheatGenerator(BlockPos pos, BlockState state) {
        this(OverGenBlocks.OVERHEAT_GENERATOR, pos, state, FloatingLong.create(Long.MAX_VALUE));
    }

    protected BlockEntityOverheatGenerator(IBlockProvider blockProvider, BlockPos pos, BlockState state, @Nonnull FloatingLong output) {
        super(blockProvider, pos, state, output);
    }

    @Nonnull
    @Override
    protected IInventorySlotHolder getInitialInventory(IContentsListener listener) {
        InventorySlotHelper builder = InventorySlotHelper.forSide(this::getDirection);
        builder.addSlot(energySlot = EnergyInventorySlot.drain(getEnergyContainer(), listener, 143, 35));
        return builder.build();
    }

    public boolean canSeeSun() {
        return seesSun;
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        // System.out.println("onUpdateServer");
        if (solarCheck == null) {
            recheckSettings();
        }
        energySlot.drainContainer();
        // Sort out if the generator can see the sun; we no longer check if it's raining here,
        // since under the new rules, we can still generate power when it's raining, albeit at a
        // significant penalty.
        seesSun = checkCanSeeSun();
        if (MekanismUtils.canFunction(this) && !getEnergyContainer().getNeeded().isZero()) {
            setActive(true);
            FloatingLong production = getProduction();
            lastProductionAmount = production.subtract(getEnergyContainer().insert(production, Action.EXECUTE, AutomationType.INTERNAL));
        } else {
            setActive(false);
            lastProductionAmount = FloatingLong.ZERO;
        }
    }

    protected void recheckSettings() {
        if (level == null) {
            return;
        }
        solarCheck = new SolarCheck(level, worldPosition);
        peakOutput = getConfiguredMax();
    }

    protected boolean checkCanSeeSun() {
        if (solarCheck == null) {
            return false;
        }
        solarCheck.recheckCanSeeSun();
        return solarCheck.canSeeSun();
    }

    public FloatingLong getProduction() {
        if (level == null) {
            return FloatingLong.ZERO;
        }
        return getConfiguredMax();
    }

    protected float getBrightnessMultiplier(@Nonnull Level world) {
        //Get the brightness of the sun; note that there are some implementations that depend on the base
        // brightness function which doesn't take into account the fact that rain can't occur in some biomes.
        //TODO: Galacticraft solar energy multiplier (see TileEntitySolarGenerator 1.12 branch).
        // Also do that for the Solar Neutron Activator and Solar Recharging Unit
        return WorldUtils.getSunBrightness(world, 1.0F);
    }

    @Override
    protected RelativeSide[] getEnergySides() {
        return new RelativeSide[]{RelativeSide.BOTTOM};
    }

    protected FloatingLong getConfiguredMax() {
        return OverGenConfig.config.overheatGeneratorGeneration.get();
    }

    @Override
    public FloatingLong getMaxOutput() {
        return peakOutput;
    }

    public FloatingLong getLastProductionAmount() {
        return lastProductionAmount;
    }

    @Override
    public void addContainerTrackers(MekanismContainer container) {
        super.addContainerTrackers(container);
        container.track(SyncableBoolean.create(this::canSeeSun, value -> seesSun = value));
        container.track(SyncableFloatingLong.create(this::getMaxOutput, value -> peakOutput = value));
        container.track(SyncableFloatingLong.create(this::getLastProductionAmount, value -> lastProductionAmount = value));
    }

    protected static class SolarCheck {

        private final boolean needsRainCheck;
        private final float peakMultiplier;
        protected final BlockPos pos;
        protected final Level world;
        protected boolean canSeeSun;

        public SolarCheck(Level world, BlockPos pos) {
            this.world = world;
            this.pos = pos;
            Biome b = this.world.getBiomeManager().getBiome(this.pos).value();
            needsRainCheck = b.getPrecipitation() != Precipitation.NONE;

            // Treat rainfall as a proxy for humidity; any humidity works as a drag on overall efficiency.
            // As with temperature, we scale it so that it doesn't overwhelm production. Note the signedness
            // on the scaling factor. Also note that we only use rainfall as a proxy if it CAN rain; some dimensions
            // (like the End) have rainfall set, but can't actually support rain.
            float humidityEff = needsRainCheck ? -0.3F * b.getDownfall() : 0;
            peakMultiplier = 1.0F + humidityEff;
        }

        public void recheckCanSeeSun() {
            canSeeSun = WorldUtils.canSeeSun(world, pos);
        }

        public boolean canSeeSun() {
            return canSeeSun;
        }

        public float getPeakMultiplier() {
            return peakMultiplier;
        }

        public float getGenerationMultiplier() {
            if (!canSeeSun) {
                return 0;
            }
            if (needsRainCheck && (this.world.isRaining() || this.world.isThundering())) {
                //If the generator is in a biome where it can rain, and it's raining penalize production by 80%
                return peakMultiplier * 0.2F;
            }
            return peakMultiplier;
        }
    }
}