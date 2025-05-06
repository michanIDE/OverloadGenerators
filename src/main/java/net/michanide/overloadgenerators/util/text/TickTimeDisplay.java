package net.michanide.overloadgenerators.util.text;

import javax.annotation.ParametersAreNonnullByDefault;

import mekanism.api.text.IHasTextComponent;
import mekanism.common.MekanismLang;
import net.michanide.overloadgenerators.OverloadGeneratorsLang;
import net.michanide.overloadgenerators.config.OverGenConfig;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TickTimeDisplay implements IHasTextComponent {

    public static final TickTimeDisplay ZERO = of(0L);

    private final long tickTime;
    private final long tickTimeThreshold;

    private TickTimeDisplay(Long tickTime) {
        Long tickTimeThreshold = OverGenConfig.config.tickTimeGeneratorThreshold.get(); 
        this.tickTime = tickTime;
        this.tickTimeThreshold = tickTimeThreshold;
    }

    public static TickTimeDisplay of(Long tickTime) {
        return new TickTimeDisplay(tickTime);
    }

    @Override
    public Component getTextComponent() {
        return MekanismLang.GENERIC_FRACTION.translate(OverloadGeneratorsLang.GENERIC_MILLISECONDS.translate(tickTime / 1_000_000L), OverloadGeneratorsLang.GENERIC_MILLISECONDS.translate(tickTimeThreshold / 1_000_000L));
    }
}
