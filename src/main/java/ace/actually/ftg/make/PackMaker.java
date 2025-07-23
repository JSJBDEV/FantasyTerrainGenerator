package ace.actually.ftg.make;

import ace.actually.ftg.FTG;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PackMaker {

    public static void main(String[] args) {
        /**File folder = new File("./run/projects/low/data.json");
        System.out.println(folder.toPath().toAbsolutePath());
        try {
            FTG.ACTIVE_MAP.extrapolateJson(folder,"low");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }**/
        makePacks();
    }

    public static void makePacks()
    {
        File folder = new File("./projects/");
        for(File dir: folder.listFiles())
        {
            try {
                makePack(dir);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void makePack(File dir) throws IOException {
        String datapackDir = "./datapacks/"+dir.getName()+"/";
        Files.createDirectory(Path.of(datapackDir));
        ImageTools.reduceColoursAdjacently(dir.getPath()+"biomes.png",datapackDir+"biomes.png");
        ImageTools.smudgeHeightmap(dir.getPath()+"heightmap.png",datapackDir+"heightmap.png");
        ImageTools.smudgeHeightmap(dir.getPath()+"aquifers.png",datapackDir+"aquifers.png");
        File file = new File(dir.getPath()+"data.json");
        FTG.ACTIVE_MAP.extrapolateJson(file,datapackDir);


    }

}
