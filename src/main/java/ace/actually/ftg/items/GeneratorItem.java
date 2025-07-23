package ace.actually.ftg.items;

import ace.actually.ftg.make.JsonTools;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;


public class GeneratorItem extends Item {
    public GeneratorItem(Settings settings) {
        super(settings);
    }
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
       if(!world.isClient)
       {

       }
        return super.use(world, user, hand);
    }
}
