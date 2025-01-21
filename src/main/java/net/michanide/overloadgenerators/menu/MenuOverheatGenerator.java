package net.michanide.overloadgenerators.menu;

import net.michanide.overloadgenerators.init.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.block.entity.BlockEntity;

public class MenuOverheatGenerator extends AbstractContainerMenu {
    private final ContainerData data;

    public MenuOverheatGenerator(int id, Inventory inventory, FriendlyByteBuf data){
        this(id, inventory ,inventory.player.level.getBlockEntity(data.readBlockPos()), null);
    }

    public MenuOverheatGenerator(int id, Inventory playerInventory, BlockEntity blockEntity, ContainerData data) {
        super(ModMenus.MENU_OVERHEAT_GENERATOR.get(), id);
        this.data = data;

        // register data slots
        addDataSlots(data);

        // player inventory
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int k = 0; k < 9; k++) {
            this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 142));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    public int getEnergyStored() {
        return data.get(0); // fetch energy stored from data
    }
}
