package net.michanide.overloadgenerators.util.text;

import javax.annotation.ParametersAreNonnullByDefault;

import mekanism.api.text.IHasTextComponent;
import mekanism.common.MekanismLang;
import net.michanide.overloadgenerators.config.OverGenConfig;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CPUUsageDisplay implements IHasTextComponent {

    public static final CPUUsageDisplay ZERO = of(0.0);

    private final long CPUUsagePercentage;
    private final long CPUUsagePercentageThreshold;
    

    private CPUUsageDisplay(Double cpuUsage) {
        Double cpuUsageThreshold = OverGenConfig.config.cpuUsageGeneratorThreshold.get();
        this.CPUUsagePercentage = Math.round(cpuUsage * 100);
        this.CPUUsagePercentageThreshold = Math.round(cpuUsageThreshold * 100);
    }

    public static CPUUsageDisplay of(Double cpuUsage) {
        return new CPUUsageDisplay(cpuUsage);
    }

    @Override
    public Component getTextComponent() {
        return MekanismLang.GENERIC_FRACTION.translate(MekanismLang.GENERIC_PERCENT.translate(CPUUsagePercentage), MekanismLang.GENERIC_PERCENT.translate(CPUUsagePercentageThreshold));
    }
}