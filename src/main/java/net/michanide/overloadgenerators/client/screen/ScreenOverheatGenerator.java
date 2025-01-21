package net.michanide.overloadgenerators.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.michanide.overloadgenerators.menu.MenuOverheatGenerator;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ScreenOverheatGenerator extends AbstractContainerScreen<MenuOverheatGenerator> {
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation("examplemod", "textures/gui/solar_generator.png");

    public ScreenOverheatGenerator(MenuOverheatGenerator menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);
        blit(poseStack, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        // render energy bar
        int energyHeight = menu.getEnergyStored() * 50 / 10000; // convert energy to height
        blit(poseStack, leftPos + 80, topPos + 20 + (50 - energyHeight), 176, 0, 16, energyHeight);
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
        font.draw(poseStack, title, titleLabelX, titleLabelY, 0x404040);
    }
}
