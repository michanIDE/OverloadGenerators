package net.michanide.overloadgenerators.blocks.tile;

import javax.annotation.Nullable;

import net.michanide.overloadgenerators.init.ModBlockEntity;
import net.michanide.overloadgenerators.menu.MenuOverheatGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;

public class BlockEntityOverheatGenerator extends BlockEntity implements MenuProvider {
    private static final int RF_PER_TICK = 20; // 1秒間に20RFを生成
    private int energyStored = 0;
    private final int capacity = 10000; // Max Energy Stored
    private final LazyOptional<IEnergyStorage> energyStorage = LazyOptional.of(this::createEnergyStorage);

    public BlockEntityOverheatGenerator(BlockPos pos, BlockState state) {
        super(ModBlockEntity.OVERHEAT_GENERATOR.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BlockEntityOverheatGenerator blockEntity) {
        // Do not tick on client side
        if (!level.isClientSide) {
            // Generate energy only during the day and when the block can see the sky
            if (level.isDay() && level.canSeeSky(pos)) {
                blockEntity.generateEnergy();
            }
        }
    }

    private void generateEnergy() {
        if (energyStored + RF_PER_TICK <= capacity) {
            energyStored += RF_PER_TICK;
        }
    }

    private IEnergyStorage createEnergyStorage() {
        return new EnergyStorage(capacity, RF_PER_TICK, 0) {
            @Override
            public int receiveEnergy(int maxReceive, boolean simulate) {
                return 0; // Not receive energy
            }

            @Override
            public int extractEnergy(int maxExtract, boolean simulate) {
                int energyExtracted = Math.min(maxExtract, energyStored);
                if (!simulate) {
                    energyStored -= energyExtracted;
                }
                return energyExtracted;
            }

            @Override
            public int getEnergyStored() {
                return energyStored;
            }

            @Override
            public int getMaxEnergyStored() {
                return capacity;
            }

            @Override
            public boolean canExtract() {
                return true;
            }

            @Override
            public boolean canReceive() {
                return false;
            }
        };
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityEnergy.ENERGY) {
            return energyStorage.cast();
        }
        return super.getCapability(cap, side);
    }

    public void drops() {
        Containers.dropContents(level, worldPosition, (SimpleContainer) energyStorage.orElse(null));
    }


    @Override
    public Component getDisplayName() {
        return new TranslatableComponent("block.examplemod.solar_generator");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new MenuOverheatGenerator(containerId, playerInventory, this, null);
    }
}
