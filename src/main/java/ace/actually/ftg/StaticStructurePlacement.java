package ace.actually.ftg;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.gen.chunk.placement.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class StaticStructurePlacement extends StructurePlacement {

    public static final Codec<StaticStructurePlacement> CODEC = Codecs.validate(RecordCodecBuilder.mapCodec((instance) -> {
        return buildCodec(instance).and(instance.group(Codec.list(Codec.INT).fieldOf("x").forGetter(StaticStructurePlacement::getX), Codec.list(Codec.INT).fieldOf("z").forGetter(StaticStructurePlacement::getZ))).apply(instance,StaticStructurePlacement::new);
    }), StaticStructurePlacement::validate).codec();




    private static DataResult<StaticStructurePlacement> validate(StaticStructurePlacement structurePlacement) {
        return DataResult.success(structurePlacement);
    }

    //

    private final List<Integer> x;
    private final List<Integer> z;
    public StaticStructurePlacement(Vec3i locateOffset, FrequencyReductionMethod frequencyReductionMethod, float frequency, int salt, Optional<ExclusionZone> exclusionZone) {
        super(locateOffset, frequencyReductionMethod, frequency, salt, exclusionZone);
        this.x=new ArrayList<>();
        this.z=new ArrayList<>();
    }
    public StaticStructurePlacement(Vec3i locateOffset, FrequencyReductionMethod frequencyReductionMethod, float frequency, int salt, Optional<ExclusionZone> exclusionZone,List<Integer> x, List<Integer> z) {
        super(locateOffset, frequencyReductionMethod, frequency, salt, exclusionZone);
        this.x=x;
        this.z=z;
    }

    @Override
    protected boolean isStartChunk(StructurePlacementCalculator calculator, int chunkX, int chunkZ) {

        for (int i = 0; i < x.size(); i++) {
            if(chunkX==x.get(i) && chunkZ==z.get(i))
            {
                //System.out.println("found a start chunk!");
                return true;
            }
        }

       return false;
    }

    @Override
    public StructurePlacementType<?> getType() {
        return FTG.STATIC_STRUCTURE_PLACEMENT;
    }

    public List<Integer> getX() {
        return x;
    }

    public List<Integer> getZ() {
        return z;
    }
}
