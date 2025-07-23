package ace.actually.ftg.blocks;

import ace.actually.ftg.FTG;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class BridgeBuilder extends Block {
    public BridgeBuilder(Settings settings) {
        super(settings);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.randomTick(state, world, pos, random);
        boolean obstructedByBridgeBuilder = false;
        for(Direction direction: Direction.values())
        {
            if(world.getBlockState(pos.offset(direction)).isOf(FTG.BRIDGE_BUILDER))
            {
                obstructedByBridgeBuilder=true;
            }
            if(world.isWater(pos.offset(direction)))
            {


                int i = 1;
                while (isWet(world,pos,direction,i) && i<200)
                {
                    i++;
                }

                if(i!=200)
                {
                    if(isNotWet(world,pos,direction,i))
                    {
                        System.out.println("bridging "+direction+" max "+i);
                        obstructedByBridgeBuilder=false;
                        int maxi = i;
                        i = 0;
                        buildBridge(i,maxi,world,pos,direction);
                    }

                }
            }
        }
        if(obstructedByBridgeBuilder)return;

        for (int i = -10; i < 10; i++) {
            for (int j = -10; j < 10; j++) {
                for (int k = -10; k < 10; k++) {
                    if(world.getBlockState(pos.add(i,j,k)).isOf(FTG.BRIDGE_BUILDER))
                    {
                        world.setBlockState(pos.add(i,j,k),Blocks.WATER.getDefaultState());
                    }
                }
            }
        }
    }

    private boolean isWet(World world, BlockPos pos, Direction direction, int i)
    {
        for (int j = 0; j < 5; j++) {
            if(world.isWater(pos.down(j).offset(direction,i)))
            {
                return true;
            }
        }
        for (int j = 0; j < 5; j++) {
            if(world.isWater(pos.up(j).offset(direction,i)))
            {
                return true;
            }
        }


        return false;
    }

    private boolean isNotWet(World world, BlockPos pos, Direction direction, int i)
    {
        for (int j = 0; j < 5; j++) {
            if(!world.isAir(pos.offset(direction,i)) && !world.isWater(pos.offset(direction,i)))
            {
                return true;
            }
        }
        for (int j = 0; j < 5; j++) {
            if(!world.isAir(pos.offset(direction,i)) && !world.isWater(pos.offset(direction,i)))
            {
                return true;
            }
        }


        return false;
    }


    private void buildBridge(int i, int maxi, World world, BlockPos pos, Direction direction)
    {
        int upness = 0;
        while (i<maxi)
        {
            if(i<(maxi/7) || i>(6*(maxi/7)))
            {
                upness = 0;
            }
            else if (i<(maxi/5) || i>(4*(maxi/5)))
            {
                upness= 1;
            }
            else if (i<(maxi/4) || i>(3*(maxi/4)))
            {
                upness= 2;
            }
            else if (i<(maxi/3) || i>(2*(maxi/3)))
            {
                upness= 3;
            }


            world.setBlockState(pos.up(upness).offset(direction,i),Blocks.OAK_PLANKS.getDefaultState());
            world.setBlockState(pos.up(upness).offset(direction,i).offset(direction.rotateClockwise(Direction.Axis.Y)),Blocks.OAK_PLANKS.getDefaultState());
            world.setBlockState(pos.up(upness).offset(direction,i).offset(direction.rotateCounterclockwise(Direction.Axis.Y)),Blocks.OAK_PLANKS.getDefaultState());
            world.setBlockState(pos.up(upness).offset(direction,i).offset(direction.rotateClockwise(Direction.Axis.Y),2),Blocks.STRIPPED_OAK_LOG.getDefaultState());
            world.setBlockState(pos.up(upness).offset(direction,i).offset(direction.rotateCounterclockwise(Direction.Axis.Y),2),Blocks.STRIPPED_OAK_LOG.getDefaultState());
            if(i%3==0)
            {
                world.setBlockState(pos.up(upness+1).offset(direction,i).offset(direction.rotateClockwise(Direction.Axis.Y),2),Blocks.OAK_FENCE.getDefaultState());
                world.setBlockState(pos.up(upness+1).offset(direction,i).offset(direction.rotateCounterclockwise(Direction.Axis.Y),2),Blocks.OAK_FENCE.getDefaultState());
            }

            i++;
        }
    }
}


