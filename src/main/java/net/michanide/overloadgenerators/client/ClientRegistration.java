package net.michanide.overloadgenerators.client;

import mekanism.client.ClientRegistrationUtil;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import net.michanide.overloadgenerators.OverloadGenerators;
import net.michanide.overloadgenerators.client.gui.GuiCPUUsageGenerator;
import net.michanide.overloadgenerators.init.OverGenContainerTypes;
import net.michanide.overloadgenerators.tile.BlockEntityCPUUsageGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = OverloadGenerators.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientRegistration {

    @SuppressWarnings("Convert2MethodRef")
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void registerContainers(RegistryEvent.Register<MenuType<?>> event) {
        ClientRegistrationUtil.registerScreen(OverGenContainerTypes.CPU_USAGE_GENERATOR, (MekanismTileContainer<BlockEntityCPUUsageGenerator> container, Inventory inv, Component title) -> new GuiCPUUsageGenerator<>(container, inv, title));
        System.out.println("Registered container");
    }
    
}
