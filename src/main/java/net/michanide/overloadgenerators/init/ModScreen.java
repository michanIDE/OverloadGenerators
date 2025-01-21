package net.michanide.overloadgenerators.init;

import net.michanide.overloadgenerators.client.screen.ScreenOverheatGenerator;
import net.minecraft.client.gui.screens.MenuScreens;

public class ModScreen {
    public static void screenSetup() {
        MenuScreens.register(ModMenus.MENU_OVERHEAT_GENERATOR.get(), ScreenOverheatGenerator::new);
    }
}
