# Fantasy Terrain Generator
An attempt at being able to load [Azgaar's Fantasy Map](https://azgaar.github.io/Fantasy-Map-Generator/) into minecraft


## How To
Currently, you need to create aquifers.png, biomes.png, biomes_no_roads.png and heightmap.png.

The example maps are exported at around 15,000 by 7,500 pixels. 1 pixel is about 2 blocks.

To generate biomes.png use the preset in this repo FTGFlatRoutes.json, enable layers Biomes, Rivers and Routes and disable the rest

To generate biomes_no_roads.png use the same preset but disable the Routes layer

To generate aquifers.png use preset FTGHeightmap (6).json, enable Heightmap and Biomes layers and siable the rest

To generate heightmap.png use the same preset but also enable Rivers

You also need to export full json from your map and place it in the data/ftg/ftgjson/data.json file

Then put all your files in `run/projects/FOLDER/` (you can name the FOLDER) and run PackMaker as a normal Java program, it will create a datapack for your map.

if done correctly, you should be able to `/execute in ftg:ftg run tp @p ~ ~ ~` to see your map as a minecraft world