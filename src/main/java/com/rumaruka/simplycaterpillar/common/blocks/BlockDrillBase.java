package com.rumaruka.simplycaterpillar.common.blocks;


import com.rumaruka.simplycaterpillar.Caterpillar;
import com.rumaruka.simplycaterpillar.Config;
import com.rumaruka.simplycaterpillar.Const;
import com.rumaruka.simplycaterpillar.common.tileentity.TileEntityDrillHead;
import com.rumaruka.simplycaterpillar.common.tileentity.TileEntityDrillPart;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Hashtable;
import java.util.Random;

public class BlockDrillBase extends BlockContainer {
    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    public Hashtable<BlockPos, Integer> pushticks;
    public int movementTicks;
    protected BlockDrillBase( ) {
        super(Material.IRON);
        setSoundType(SoundType.STONE);
        setHardness(0.5f);
        pushticks=new Hashtable<>();
        movementTicks=25;
        setHarvestLevel("pickaxe",0);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityDrillPart();
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {

    }

    @Override
    public void onBlockExploded(World world, BlockPos pos, Explosion explosion) {

    }
    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        return false;
    }
    public void calculateMovement(World worldIn, BlockPos pos, IBlockState state)
    {
        try {
            if (!worldIn.isRemote)
            {
                if (!(worldIn.getBlockState(pos).getBlock() instanceof BlockDrillBase))
                {
                    worldIn.removeTileEntity(pos);
                    Const.printDebug("Tile Entity: Removed " + pos.toString());
                    return;
                }

                int[] movingXZ = Caterpillar.instance.getWayMoving(state);
                boolean okToMove = false;

                if (worldIn.getBlockState(pos.add(movingXZ[0], 0, movingXZ[1])).equals(Blocks.AIR.getDefaultState()) ||
                        worldIn.getBlockState(pos.add(movingXZ[0], 0, movingXZ[1])).getBlock().equals(Blocks.FLOWING_LAVA) ||
                        worldIn.getBlockState(pos.add(movingXZ[0], 0, movingXZ[1])).getBlock().equals(Blocks.FLOWING_WATER) ||
                        worldIn.getBlockState(pos.add(movingXZ[0], 0, movingXZ[1])).getBlock().equals(Blocks.WATER) ||
                        worldIn.getBlockState(pos.add(movingXZ[0], 0, movingXZ[1])).getBlock().equals(Blocks.LAVA))
                {
                    okToMove = true;
                }
                if (okToMove && worldIn.getBlockState(pos.add(movingXZ[0]*2, 0, movingXZ[1]*2)).getBlock() instanceof BlockDrillBase)
                {
                    if (!this.pushticks.containsKey(pos))
                    {
                        this.pushticks.put(pos, 0);
                    }

                    int counter = this.pushticks.get(pos);
                    counter++;
                    this.pushticks.put(pos, counter);
                    Random rnd = new Random(System.currentTimeMillis());
                    if (counter < 10 + rnd.nextInt(10))
                    {
                        return;
                    }
                    this.pushticks.remove(pos);

                    BlockPos newPlace = pos.add(movingXZ[0], 0, movingXZ[1]);
                    TargetPoint targetPoint = new TargetPoint(worldIn.getWorldType().getId(), newPlace.getX(), newPlace.getY(), newPlace.getZ(), 5);
                    PacketDispatcher.sendToAllAround(new PacketParticles(EnumParticleTypes.FIREWORKS_SPARK.name(), newPlace.getX(), newPlace.getY(), newPlace.getZ()), targetPoint);

                    worldIn.setBlockState(newPlace, state);
                    worldIn.setBlockToAir(pos);
                    if(Config.enablesounds) {
                        worldIn.playSound(null, pos, SoundEvents.BLOCK_PISTON_EXTEND, SoundCategory.BLOCKS, 0.5F, worldIn.rand.nextFloat() * 0.25F + 0.6F);
                    }

                    int count = this.getCountIndex(movingXZ, newPlace);
                    TileEntityDrillHead cater = Caterpillar.getCaterpillar(worldIn, pos, state.getValue(FACING));
                    if (cater != null)
                    {
                        this.fired(worldIn, newPlace, state, cater, movingXZ, count);
                        this.setDrag(cater);
                        cater.headTick = 0;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
