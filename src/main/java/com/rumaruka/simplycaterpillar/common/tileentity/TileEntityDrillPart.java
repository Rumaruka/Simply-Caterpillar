package com.rumaruka.simplycaterpillar.common.tileentity;

import com.rumaruka.simplycaterpillar.Const;
import com.rumaruka.simplycaterpillar.common.blocks.BlockDrillBase;
import com.rumaruka.simplycaterpillar.common.blocks.BlockDrillHeads;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;

public class TileEntityDrillPart extends TileEntity implements ITickable {
    public TileEntityDrillPart()
    {
        Const.printDebug("Initializing Caterpillar Part");
    }

    @Override
    public void update() {
        IBlockState blockdriller =  this.world.getBlockState(this.pos);

        if (blockdriller.getBlock() instanceof BlockDrillBase || blockdriller.getBlock() instanceof BlockDrillHeads)
        {
            ((BlockDrillBase)blockdriller.getBlock()).calculateMovement(this.world, this.pos, this.world.getBlockState(this.pos));
        }
    }
}
