package net.michanide.overloadgenerators.tile;

import mekanism.common.integration.computer.annotation.ComputerMethod;
import mekanism.common.inventory.container.MekanismContainer;
import mekanism.common.inventory.container.sync.SyncableLong;
import mekanism.common.tile.base.TileEntityMekanism;
import net.michanide.overloadgenerators.init.OverGenBlocks;
import net.michanide.overloadgenerators.util.GlobalTickHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class BlockEntityOpticalLatticeClock extends TileEntityMekanism{

    private Long tickTime = 0L;

    
    public BlockEntityOpticalLatticeClock(BlockPos pos, BlockState state) {
        super(OverGenBlocks.OPTICAL_LATTICE_CLOCK, pos, state);
    }

    @Override
    protected void onUpdateServer() {
        tickTime = GlobalTickHandler.getCachedTickTime();
        super.onUpdateServer();
    }

    @ComputerMethod
    public Long getTickTime() {
        return tickTime;
    }

    @Override
    public void addContainerTrackers(MekanismContainer container) {
        super.addContainerTrackers(container);
        container.track(SyncableLong.create(this::getTickTime, value -> tickTime = value));
    }
}
