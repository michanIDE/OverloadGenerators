package net.michanide.overloadgenerators.block;

import javax.annotation.Nonnull;

import mekanism.common.block.prefab.BlockTile;
import mekanism.common.content.blocktype.BlockTypeTile;
import mekanism.common.util.WorldUtils;
import net.michanide.overloadgenerators.OverloadGeneratorsLang;
import net.michanide.overloadgenerators.init.OverGenBlockTypes;
import net.michanide.overloadgenerators.tile.BlockEntityOpticalLatticeClock;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.network.chat.Component;

public class BlockOpticalLatticeClock extends BlockTile<BlockEntityOpticalLatticeClock, BlockTypeTile<BlockEntityOpticalLatticeClock>> {

    public BlockOpticalLatticeClock() {
        super(OverGenBlockTypes.OPTICAL_LATTICE_CLOCK);
    }
    
    @Nonnull
    @Override
    @Deprecated
    public InteractionResult use(@Nonnull BlockState state, @Nonnull Level world, @Nonnull BlockPos pos, @Nonnull Player player, @Nonnull InteractionHand hand,
          @Nonnull BlockHitResult hit) {
            super.use(state, world, pos, player, hand, hit);
        if (player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }
        BlockEntityOpticalLatticeClock tile = WorldUtils.getTileEntity(BlockEntityOpticalLatticeClock.class, world, pos);
        if (tile == null) {
            return InteractionResult.PASS;
        } else if (!world.isClientSide()) {
            Long tickTime = tile.getTickTime();
            Component text = OverloadGeneratorsLang.TICK_TIME.translate(OverloadGeneratorsLang.GENERIC_NANOSECONDS.translate(tickTime));
            
            player.sendMessage(text, Util.NIL_UUID);
        }
        return InteractionResult.sidedSuccess(world.isClientSide);
    }
}
