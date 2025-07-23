package ace.actually.ftg;

import ace.actually.ftg.blocks.BridgeBuilder;
import ace.actually.ftg.items.GeneratorItem;
import ace.actually.ftg.make.JsonTools;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.miir.atlas.Atlas;
import com.miir.atlas.world.gen.NamespacedMapImage;
import com.mojang.serialization.Codec;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.registry.*;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.PlaceCommand;
import net.minecraft.structure.StructureSet;
import net.minecraft.structure.StructureSets;
import net.minecraft.structure.VillageGenerator;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.placement.SpreadType;
import net.minecraft.world.gen.chunk.placement.StructurePlacement;
import net.minecraft.world.gen.chunk.placement.StructurePlacementType;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.FreezeTopLayerFeature;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class FTG implements ModInitializer {
	public static final String MOD_ID = "ftg";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final StructurePlacementType<StaticStructurePlacement> STATIC_STRUCTURE_PLACEMENT = Registry.register(Registries.STRUCTURE_PLACEMENT,Identifier.of("ftg","static"),()->StaticStructurePlacement.CODEC);
	public static final PaveRoadsFeature PAVE_ROADS_FEATURE = Registry.register(Registries.FEATURE,Identifier.of("ftg","pave_roads"),new PaveRoadsFeature(DefaultFeatureConfig.CODEC));
	public static final MakeTrailsFeature MAKE_TRAILS_FEATURE = Registry.register(Registries.FEATURE,Identifier.of("ftg","make_trails"),new MakeTrailsFeature(DefaultFeatureConfig.CODEC));


	public static JsonTools ACTIVE_MAP;

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		registerBlocks();
		registerItems();
		LOGGER.info("Hello Fabric world!");

		ServerLifecycleEvents.SERVER_STARTED.register(minecraftServer ->
		{
			try {
				BufferedReader d = minecraftServer.getResourceManager().getResource(new Identifier("ftg","ftgjson/data.json")).get().getReader();
				JsonObject data  = (JsonObject) JsonParser.parseReader(d);
				ACTIVE_MAP = new JsonTools(data);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});

	}

	public static final GeneratorItem GENERATOR_ITEM = new GeneratorItem(new Item.Settings());
	private void registerItems()
	{
		Registry.register(Registries.ITEM, Identifier.of("ftg","gen"),GENERATOR_ITEM);
	}

	public static final BridgeBuilder BRIDGE_BUILDER = new BridgeBuilder(AbstractBlock.Settings.create().ticksRandomly());
	private void registerBlocks()
	{
		Registry.register(Registries.BLOCK,Identifier.of("ftg","bridge_builder"),BRIDGE_BUILDER);
	}
}