package net.michanide.overloadgenerators.capability;

import java.util.Objects;
import java.util.function.Predicate;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
import mekanism.api.annotations.FieldsAreNonnullByDefault;
import mekanism.api.annotations.NonNull;
import mekanism.api.math.FloatingLong;
import mekanism.common.capabilities.energy.BasicEnergyContainer;
import net.minecraft.MethodsReturnNonnullByDefault;

@FieldsAreNonnullByDefault
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class OverGenEnergyContainer extends BasicEnergyContainer {

    public static OverGenEnergyContainer create(FloatingLong maxEnergy, @Nullable IContentsListener listener) {
        Objects.requireNonNull(maxEnergy, "Max energy cannot be null");
        return new OverGenEnergyContainer(maxEnergy, alwaysTrue, alwaysTrue, listener);
    }

    public static OverGenEnergyContainer input(FloatingLong maxEnergy, @Nullable IContentsListener listener) {
        Objects.requireNonNull(maxEnergy, "Max energy cannot be null");
        return new OverGenEnergyContainer(maxEnergy, notExternal, alwaysTrue, listener);
    }

    public static OverGenEnergyContainer output(FloatingLong maxEnergy, @Nullable IContentsListener listener) {
        Objects.requireNonNull(maxEnergy, "Max energy cannot be null");
        return new OverGenEnergyContainer(maxEnergy, alwaysTrue, internalOnly, listener);
    }

    public static OverGenEnergyContainer create(FloatingLong maxEnergy, Predicate<@NonNull AutomationType> canExtract, Predicate<@NonNull AutomationType> canInsert,
          @Nullable IContentsListener listener) {
        Objects.requireNonNull(maxEnergy, "Max energy cannot be null");
        Objects.requireNonNull(canExtract, "Extraction validity check cannot be null");
        Objects.requireNonNull(canInsert, "Insertion validity check cannot be null");
        return new OverGenEnergyContainer(maxEnergy, canExtract, canInsert, listener);
    }

    private FloatingLong currentMaxEnergy;

    protected OverGenEnergyContainer(FloatingLong maxEnergy, Predicate<@NonNull AutomationType> canExtract,
          Predicate<@NonNull AutomationType> canInsert, @Nullable IContentsListener listener) {
        super(maxEnergy, canExtract, canInsert, listener);
        currentMaxEnergy = getBaseMaxEnergy();
    }

    public boolean adjustableRates() {
        return false;
    }

    @Override
    public FloatingLong getMaxEnergy() {
        return currentMaxEnergy;
    }

    public FloatingLong getBaseMaxEnergy() {
        return super.getMaxEnergy();
    }

    public void setMaxEnergy(FloatingLong maxEnergy) {
        Objects.requireNonNull(maxEnergy, "Max energy cannot be null");
        this.currentMaxEnergy = maxEnergy;
        if (getEnergy().greaterThan(getMaxEnergy())) {
            setEnergy(getMaxEnergy());
        }
    }
}