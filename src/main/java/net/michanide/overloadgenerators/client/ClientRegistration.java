package net.michanide.overloadgenerators.client;

import mekanism.client.ClientRegistrationUtil;
import net.michanide.overloadgenerators.OverloadGenerators;
import net.michanide.overloadgenerators.client.gui.GuiCPUUsageGenerator;
import net.michanide.overloadgenerators.client.gui.GuiMemoryUsageGenerator;
import net.michanide.overloadgenerators.client.gui.GuiServerCrashGenerator;
import net.michanide.overloadgenerators.client.gui.GuiTickTimeGenerator;
import net.michanide.overloadgenerators.init.OverGenContainerTypes;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegisterEvent;

@Mod.EventBusSubscriber(modid = OverloadGenerators.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientRegistration {

    @SuppressWarnings("Convert2MethodRef")
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void registerContainers(RegisterEvent event) {
        event.register(Registries.MENU, helper -> {
            ClientRegistrationUtil.registerScreen(OverGenContainerTypes.CPU_USAGE_GENERATOR, GuiCPUUsageGenerator::new);
            ClientRegistrationUtil.registerScreen(OverGenContainerTypes.MEMORY_USAGE_GENERATOR, GuiMemoryUsageGenerator::new);
            ClientRegistrationUtil.registerScreen(OverGenContainerTypes.TICK_TIME_GENERATOR, GuiTickTimeGenerator::new);
            ClientRegistrationUtil.registerScreen(OverGenContainerTypes.SERVER_CRASH_GENERATOR, GuiServerCrashGenerator::new);
        });
    }
    
}
