package net.michanide.overloadgenerators.util.text;

import javax.annotation.ParametersAreNonnullByDefault;

import mekanism.api.text.IHasTextComponent;
import mekanism.common.MekanismLang;
import net.michanide.overloadgenerators.config.OverGenConfig;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MemoryUsageDisplay implements IHasTextComponent {

    public static final MemoryUsageDisplay ZERO = of(0.0);

    private final long MemoryUsagePercentage;
    private final long MemoryUsagePercentageThreshold;
    

    private MemoryUsageDisplay(Double memoryUsage) {
        Double memoryUsageThreshold = OverGenConfig.config.memoryUsageGeneratorThreshold.get();
        this.MemoryUsagePercentage = Math.round(memoryUsage * 100);
        this.MemoryUsagePercentageThreshold = Math.round(memoryUsageThreshold * 100);
    }

    public static MemoryUsageDisplay of(Double memoryUsage) {
        return new MemoryUsageDisplay(memoryUsage);
    }

    @Override
    public Component getTextComponent() {
        return MekanismLang.GENERIC_FRACTION.translate(MekanismLang.GENERIC_PERCENT.translate(MemoryUsagePercentage), MekanismLang.GENERIC_PERCENT.translate(MemoryUsagePercentageThreshold));
    }
}