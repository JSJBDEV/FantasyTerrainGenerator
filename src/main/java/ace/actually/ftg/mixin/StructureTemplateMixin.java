package ace.actually.ftg.mixin;

import ace.actually.ftg.FTG;
import net.minecraft.block.Blocks;
import net.minecraft.block.PillarBlock;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.structure.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(StructureTemplate.class)
public abstract class StructureTemplateMixin {

    @Inject(at = @At("RETURN"), method = "process", cancellable = true)
    private static void init(ServerWorldAccess world, BlockPos pos, BlockPos pivot, StructurePlacementData placementData, List<StructureTemplate.StructureBlockInfo> infos, CallbackInfoReturnable<List<StructureTemplate.StructureBlockInfo>> cir) {
        if(world.toServerWorld().getRegistryKey().getValue().getNamespace().equals("ftg"))
        {
            List<StructureTemplate.StructureBlockInfo> finalised = cir.getReturnValue();
            List<StructureTemplate.StructureBlockInfo> additions = new ArrayList<>();

            boolean house = false;
            boolean path = false;
            boolean carvedPath = true;

            for(StructureTemplate.StructureBlockInfo info: finalised)
            {
                if(!info.state().isOf(Blocks.OAK_PLANKS))
                {
                    carvedPath=false;
                }
                if(info.state().isOf(Blocks.FARMLAND) && world.isWater(info.pos().up()))
                {
                    cir.setReturnValue(new ArrayList<>());
                    return;
                }
                if(info.state().isOf(Blocks.TORCH) || info.state().isOf(Blocks.WALL_TORCH) || info.state().isOf(Blocks.YELLOW_WOOL))
                {
                    house = true;
                    break;
                }
                if(info.state().isOf(Blocks.DIRT_PATH))
                {
                    path = true;
                    break;
                }
                if(info.state().isOf(Blocks.BELL))
                {
                    System.out.println("bell");
                }
            }

            if(house)
            {
                for(StructureTemplate.StructureBlockInfo info: finalised)
                {
                    BlockPos b = info.pos();
                    if(world.isWater(b.east()) || world.isWater(b.west()) || world.isWater(b.north()) || world.isWater(b.south()))
                    {
                        cir.setReturnValue(new ArrayList<>());
                        return;
                    }

                    if(world.getBlockState(b.down()).isAir())
                    {
                        if((info.state().isIn(BlockTags.LOGS) && info.state().get(PillarBlock.AXIS).isVertical()) || info.state().isOf(Blocks.GRASS_BLOCK))
                        {
                            int v = 1;
                            while (world.getBlockState(b.down(v)).isAir())
                            {
                                additions.add(new StructureTemplate.StructureBlockInfo(b.down(v),Blocks.COBBLESTONE.getDefaultState(),new NbtCompound()));
                                v++;
                            }
                            additions.add(new StructureTemplate.StructureBlockInfo(b.down(v),Blocks.COBBLESTONE.getDefaultState(),new NbtCompound()));
                        }
                    }
                }
            }
            if(path || carvedPath)
            {

                finalised = finalised.stream().map(info->
                {
                    if(world.isWater(info.pos().down()))
                    {
                        if(info.pos().getY()>=world.getSeaLevel() || world.isWater(info.pos().up()))
                        {
                            return new StructureTemplate.StructureBlockInfo(info.pos(),Blocks.WATER.getDefaultState(),info.nbt());
                        }
                        return new StructureTemplate.StructureBlockInfo(info.pos(),Blocks.OAK_PLANKS.getDefaultState(),info.nbt());
                    }
                    if(checkForAdjacentWaterNoUp(world,info.pos()))
                    {
                        return new StructureTemplate.StructureBlockInfo(info.pos(), FTG.BRIDGE_BUILDER.getDefaultState(),info.nbt());
                    }
                    return info;
                }).toList();
            }

            additions.addAll(finalised);
            cir.setReturnValue(additions);

        }
    }

    @Unique
    private static boolean checkForAdjacentWaterNoUp(ServerWorldAccess world, BlockPos pos)
    {
        if(world.isWater(pos.up()))
        {
            return false;
        }

        if(world.isWater(pos.east()) || world.isWater(pos.west()) || world.isWater(pos.north()) || world.isWater(pos.south()))
        {
            return true;
        }
        return false;
    }

}
