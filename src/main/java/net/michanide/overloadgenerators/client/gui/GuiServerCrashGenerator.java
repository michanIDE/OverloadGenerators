package net.michanide.overloadgenerators.client.gui;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import mekanism.client.gui.GuiMekanismTile;
import mekanism.client.gui.element.GuiInnerScreen;
import mekanism.client.gui.element.bar.GuiVerticalPowerBar;
import mekanism.client.gui.element.tab.GuiEnergyTab;
import mekanism.common.MekanismLang;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.util.text.EnergyDisplay;
import mekanism.generators.common.GeneratorsLang;
import net.michanide.overloadgenerators.OverloadGeneratorsLang;
import net.michanide.overloadgenerators.handlers.ServerLifecycleHandler;
import net.michanide.overloadgenerators.tile.BlockEntityServerCrashGenerator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class GuiServerCrashGenerator extends GuiMekanismTile<BlockEntityServerCrashGenerator, MekanismTileContainer<BlockEntityServerCrashGenerator>> {

    public GuiServerCrashGenerator(MekanismTileContainer<BlockEntityServerCrashGenerator> container, Inventory inv, Component title) {
        super(container, inv, title);
        dynamicSlots = true;
    }

    @Override
    protected void addGuiElements() {
        super.addGuiElements();
        addRenderableWidget(new GuiInnerScreen(this, 40, 22, 96, 45, () -> {
            List<Component> list = new ArrayList<>();
            list.add(EnergyDisplay.of(tile.getEnergyContainer()).getTextComponent());
            list.add(OverloadGeneratorsLang.CRASH_COUNT.translate(tile.getTileCrashCount(), ServerLifecycleHandler.getTotalCrashCount()));
            list.add(GeneratorsLang.OUTPUT_RATE_SHORT.translate(EnergyDisplay.of(tile.getMaxOutput())));
            return list;
        }));
        addRenderableWidget(new GuiEnergyTab(this, () -> List.of(GeneratorsLang.PRODUCING_AMOUNT.translate(EnergyDisplay.of(tile.getProductionRate())),
              MekanismLang.MAX_OUTPUT.translate(EnergyDisplay.of(tile.getMaxOutput())))));
        addRenderableWidget(new GuiVerticalPowerBar(this, tile.getEnergyContainer(), 164, 15));
    }

    @Override
    protected void drawForegroundText(@NotNull GuiGraphics matrix, int mouseX, int mouseY) {
        renderTitleText(matrix);
        drawString(matrix, playerInventoryTitle, inventoryLabelX, inventoryLabelY, titleTextColor());
        super.drawForegroundText(matrix, mouseX, mouseY);
    }
}