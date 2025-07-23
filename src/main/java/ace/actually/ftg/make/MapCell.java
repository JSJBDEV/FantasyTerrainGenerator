package ace.actually.ftg.make;

import com.google.gson.JsonArray;

import java.awt.*;

public class MapCell {

    Polygon polygon;
    int state;
    int province;
    int[] adjacent;

    public MapCell(Polygon polygon, int state, int province, JsonArray adjsont)
    {
        this.polygon=polygon;
        this.province=province;
        this.state=state;
        adjacent = new int[adjsont.size()];
        for (int i = 0; i < adjsont.size(); i++) {
            adjacent[i]=adjsont.get(i).getAsInt();
        }
    }

    public int getState() {
        return state;
    }

    public int getProvince() {
        return province;
    }

    public Polygon getPolygon() {
        return polygon;
    }

    public int[] getAdjacent() {
        return adjacent;
    }
}
