package net.michanide.overloadgenerators.init;

import net.michanide.overloadgenerators.OverloadGenerators;
import net.michanide.overloadgenerators.menu.MenuOverheatGenerator;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.CONTAINERS, OverloadGenerators.MOD_ID);
    public static final RegistryObject<MenuType<MenuOverheatGenerator>> MENU_OVERHEAT_GENERATOR = MENUS.register("menu_overheat_generator", () -> IForgeMenuType.create(MenuOverheatGenerator::new));
    
    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
