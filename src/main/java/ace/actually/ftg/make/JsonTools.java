package ace.actually.ftg.make;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import org.apache.commons.io.FileUtils;

import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class JsonTools {

    //map size divide 2
    public final int XCORNER = 7516;
    public final int YCORNER = 3712;
    private List<MapCell> CELLS = new ArrayList<>();
    private Map<String,Integer> LAST_CELL = new HashMap<>();

    private JsonObject STORE;
    public JsonTools(JsonObject store)
    {
        STORE=store;
        populatePolygons();
        System.out.println(CELLS.size());
    }


    public void populatePolygons()
    {
        JsonObject pack = STORE.getAsJsonObject("pack");
        JsonArray cells = pack.getAsJsonArray("cells");
        JsonArray vertices = pack.getAsJsonArray("vertices");
        for (int i = 0; i < cells.size(); i++) {
            JsonObject cell = cells.get(i).getAsJsonObject();
            JsonArray v = cell.getAsJsonArray("v");
            JsonArray adjacent = cell.getAsJsonArray("c");
            Polygon polygon = new Polygon();

            for (int j = 0; j < v.size(); j++) {
                int vertex = v.get(j).getAsInt();
                JsonObject vertexDef = vertices.get(vertex).getAsJsonObject();
                JsonArray point = vertexDef.getAsJsonArray("p");
                polygon.addPoint(tX(point.get(0).getAsInt()),tY(point.get(1).getAsInt()));
            }
            CELLS.add(new MapCell(polygon,cell.get("state").getAsInt(),cell.get("province").getAsInt(),adjacent));
        }
    }

    /**
     * mainly just shorthand to get province and state names
     * @param sub
     * @param id
     * @param key
     * @return
     */
    public String getStringEsoterically(String sub, int id, String key)
    {
        JsonElement element = STORE.getAsJsonObject("pack").getAsJsonArray(sub).get(id);
        if(element.isJsonObject())
        {
            if(element.getAsJsonObject().keySet().contains(key))
            {
                return element.getAsJsonObject().get(key).getAsString();
            }

        }
        return "The Sea";
    }

    public MapCell getLastMapCell(String UUID)
    {
        if(LAST_CELL.containsKey(UUID))
        {
            return CELLS.get(LAST_CELL.get(UUID));
        }
        return null;
    }

    public MapCell getMapCell(BlockPos pos, String UUID)
    {
        //If we know where the "entity" was previously located, rather than checking all 4000+ cells we can instead
        //just check the last checked cell, the adjacent cells, and the cells adjacent to those ones.
        if(LAST_CELL.containsKey(UUID))
        {
            int i = LAST_CELL.get(UUID);
            if(CELLS.get(i).getPolygon().contains(pos.getX(),pos.getZ()))
            {
                //if we are in the same cell we where last time, then It's not really that interesting
                return null;
            }

            int[] adjs = CELLS.get(i).getAdjacent();
            for(int adj: adjs)
            {
                MapCell cell = CELLS.get(adj);
                if(cell.getPolygon().contains(pos.getX(),pos.getZ()))
                {
                    LAST_CELL.put(UUID,adj);
                    return cell;
                }
                for(int adjadj: cell.getAdjacent())
                {
                    MapCell cell2 = CELLS.get(adjadj);
                    if(cell2.getPolygon().contains(pos.getX(),pos.getZ()))
                    {
                        LAST_CELL.put(UUID,adjadj);
                        return cell2;
                    }
                }
            }
        }

        //If we reach here, either the "entity" is not within the map area, the game has just loaded, or they
        //have moved really fast.
        System.out.println("looking for start cell");
        for(int i = 0; i<CELLS.size(); i++ )
        {
            if(CELLS.get(i).getPolygon().contains(pos.getX(),pos.getZ()))
            {
                LAST_CELL.put(UUID,i);
                System.out.println("found at cell "+i);
                return CELLS.get(i);
            }
        }
        return null;
    }


    /**
     * translate Azgaar's X coordinate to minecraft.
     * @param x
     * @return
     */
    public float tX(float x)
    {
        return ((x*8)- XCORNER)*2;
    }
    public int tX(int x)
    {
        return ((x*8)- XCORNER)*2;
    }

    /**
     * translate Azgaar's Y coordinate to minecraft.
     * @param y
     * @return
     */
    public float tY(float y)
    {
        return ((y*8)- YCORNER)*2;
    }
    public int tY(int y)
    {
        return ((y*8)- YCORNER)*2;
    }

    public void extrapolateJson(File file, String packName) throws IOException {


        JsonObject object = (JsonObject) JsonParser.parseReader(new FileReader(file));
        List<JsonElement> v = object.getAsJsonObject("pack").getAsJsonArray("burgs").asList();
        List<Float> xs = new ArrayList<>();
        List<Float> ys = new ArrayList<>();
        for(JsonElement key: v)
        {
            if(key!=null)
            {
                JsonObject jsonObject = key.getAsJsonObject();
                JsonElement xe = jsonObject.get("x");
                if(xe!=null)
                {
                    xs.add(xe.getAsFloat());
                }
                JsonElement ye = jsonObject.get("y");
                if(ye!=null)
                {
                    ys.add(ye.getAsFloat());
                }
            }
        }
        StringBuilder xout = new StringBuilder();
        for(float f: xs)
        {
            xout.append(",").append(ChunkSectionPos.getSectionCoord(tX(f)));
        }
        StringBuilder yout = new StringBuilder();
        for(float f: ys)
        {
            yout.append(",").append(ChunkSectionPos.getSectionCoord(tY(f)));
        }


        List<String> writable = List.of("{\n" +
                "  \"structures\": [\n" +
                "    {\n" +
                "      \"structure\": \"minecraft:village_plains\",\n" +
                "      \"weight\": 1\n" +
                "    }\n" +
                "  ],\n" +
                "  \"placement\": {\n" +
                "    \"type\": \"ftg:static\",\n" +
                "    \"x\": [XS],\n".replace("XS", xout.substring(1)) +
                "    \"z\": [YS],\n".replace("YS", yout.substring(1)) +
                "\t\"salt\": 0\n" +
                "\n" +
                "  }\n" +
                "}");

        String datapackDir = "./run/datapacks/"+packName+"/";
        FileUtils.writeLines(new File(datapackDir+"worldgen/structure_set/"+packName+".json"),writable);
    }
}
