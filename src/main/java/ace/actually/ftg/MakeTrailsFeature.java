package ace.actually.ftg;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class MakeTrailsFeature extends Feature<DefaultFeatureConfig> {
    public MakeTrailsFeature(Codec<DefaultFeatureConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        StructureWorldAccess structureWorldAccess = context.getWorld();
        BlockPos blockPos = context.getOrigin();
        for (int i = -16; i < 16; i++) {
            for (int j = -16; j < 16; j++) {
                BlockPos face = blockPos.add(i,0,j);
                int m = structureWorldAccess.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, face.getX(),face.getZ());
                face = new BlockPos(face.getX(),m-1,face.getZ());

                if(structureWorldAccess.getBiome(face).getKey().get().getValue().getNamespace().equals("ftg"))
                {
                    if(structureWorldAccess.isAir(face) || structureWorldAccess.getBlockState(face).isIn(BlockTags.AZALEA_GROWS_ON))
                    {
                        switch (structureWorldAccess.getRandom().nextInt(3))
                        {
                            case 0 -> structureWorldAccess.setBlockState(face, Blocks.DIRT_PATH.getDefaultState(), Block.NOTIFY_LISTENERS);
                            case 1 -> structureWorldAccess.setBlockState(face, Blocks.COARSE_DIRT.getDefaultState(), Block.NOTIFY_LISTENERS);
                        }
                    }

                }


            }
        }
        return true;
    }
}
