package ace.actually.ftg.make;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class ImageTools {

    private  static  final List<Color> BIOMES = List.of(
            hex("fbe79f"),
            hex("b5b887"),
            hex("d2d082"),
            hex("c8d68f"),
            hex("b6d95d"),
            hex("29bc56"),
            hex("7dcb35"),
            hex("409c43"),
            hex("4b6b32"),
            hex("FF0000"),//trails
            hex("EE00FF"),//roads
            hex("96784b"),
            hex("d5e7eb"),
            hex("0b9131"),
            hex("005f69"), //lakes
            hex("0080ff"), //rivers
            hex("00fbff"), //ocean
            hex("e8f0f6")); //ice

    public static void main(String[] args) {
        try {
            String path = "PATH\\FTG\\src\\main\\resources\\data\\ftg\\atlas\\map\\biomes_roads.png";
            reduceColoursAdjacently(path,path.replace(".png","_cleared.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static void reduceColours(String path) throws IOException {

        BufferedImage baseF = ImageIO.read(new File(path));

        BufferedImage img= new BufferedImage(baseF.getWidth(), baseF.getHeight(), BufferedImage.TYPE_INT_ARGB);
        img.getGraphics().drawImage(baseF, 0, 0, null);

        BufferedImage nimg= new BufferedImage(baseF.getWidth(), baseF.getHeight(), BufferedImage.TYPE_INT_ARGB);
        nimg.getGraphics().drawImage(baseF, 0, 0, null);



        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                Color closest = Color.BLACK;

                int[] dist = new int[]{300,300,300};
                Color sc = new Color(img.getRGB(i,j));
                for(Color biome: BIOMES)
                {

                    if(sc.equals(biome))
                    {
                        closest=biome;
                        break;
                    }
                    else
                    {
                        int[] test = new int[3];
                        if(sc.getRed()>biome.getRed())
                        {
                            test[0]= sc.getRed()-biome.getRed();
                        }
                        else
                        {
                            test[0]= biome.getRed()-sc.getRed();
                        }

                        if(sc.getGreen()>biome.getGreen())
                        {
                            test[1]= sc.getGreen()-biome.getGreen();
                        }
                        else
                        {
                            test[1]= biome.getGreen()-sc.getGreen();
                        }

                        if(sc.getBlue()>biome.getBlue())
                        {
                            test[2]= sc.getBlue()-biome.getBlue();
                        }
                        else
                        {
                            test[2]= biome.getBlue()-sc.getBlue();
                        }
                        if((test[0]+test[1]+test[2]<dist[0]+dist[1]+dist[2]))
                        {
                            dist=new int[]{test[0],test[1],test[2]};
                            closest=biome;
                        }
                    }
                }
                nimg.setRGB(i,j,closest.getRGB());
            }

        }
        ImageIO.write(nimg,"png",new File(path.replace(".png","_reduced.png")));

    }


    public static void reduceColoursAdjacently(String path,String out) throws IOException {

        BufferedImage baseF = ImageIO.read(new File(path));

        BufferedImage img= new BufferedImage(baseF.getWidth(), baseF.getHeight(), BufferedImage.TYPE_INT_ARGB);
        img.getGraphics().drawImage(baseF, 0, 0, null);

        BufferedImage nimg= new BufferedImage(baseF.getWidth(), baseF.getHeight(), BufferedImage.TYPE_INT_ARGB);
        nimg.getGraphics().drawImage(baseF, 0, 0, null);


        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {


                Color sc = new Color(img.getRGB(i,j));
                for(Color biome: BIOMES)
                {

                    if(sc.equals(biome))
                    {
                        break;
                    }

                    int left = -1,right = -1,up = -1,down = -1;
                    if(i>0)
                    {
                        left=img.getRGB(i-1,j);
                    }
                    if(i<img.getWidth()-1)
                    {
                        right=img.getRGB(i+1,j);
                    }
                    if(j>0)
                    {
                        up=img.getRGB(i,j-1);
                    }
                    if(j<img.getHeight()-1)
                    {
                       down=img.getRGB(i,j+1);
                    }
                    if(biome.getRGB()==left || biome.getRGB()==right || biome.getRGB()==up || biome.getRGB()==down)
                    {
                        nimg.setRGB(i,j,biome.getRGB());
                    }

                }



            }

        }

        ImageIO.write(nimg,"png",new File(out));

    }

    public static void smudgeHeightmap(String path,String out) throws IOException {

        BufferedImage baseF = ImageIO.read(new File(path));

        BufferedImage img= new BufferedImage(baseF.getWidth(), baseF.getHeight(), BufferedImage.TYPE_INT_ARGB);
        img.getGraphics().drawImage(baseF, 0, 0, null);

        BufferedImage nimg= new BufferedImage(baseF.getWidth(), baseF.getHeight(), BufferedImage.TYPE_INT_ARGB);
        nimg.getGraphics().drawImage(baseF, 0, 0, null);


        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                Color color = new Color(img.getRGB(i,j));

                if(i+1<img.getWidth())
                {
                    Color color2 = new Color(img.getRGB(i+1,j));
                    if(color2.getRed()-color.getRed()>10)
                    {
                        nimg.setRGB(i+1,j,new Color(color2.getRed()-1,color2.getBlue()-1,color2.getGreen()-1).getRGB());
                        nimg.setRGB(i,j,new Color(color.getRed()+1,color.getBlue()+1,color.getGreen()+1).getRGB());
                    }
                }
                if(i-1>-1)
                {
                    Color color2 = new Color(img.getRGB(i-1,j));
                    if(color2.getRed()-color.getRed()>10)
                    {
                        nimg.setRGB(i-1,j,new Color(color2.getRed()-1,color2.getBlue()-1,color2.getGreen()-1).getRGB());
                        nimg.setRGB(i,j,new Color(color.getRed()+1,color.getBlue()+1,color.getGreen()+1).getRGB());
                    }
                }
                if(j+1<img.getHeight())
                {
                    Color color2 = new Color(img.getRGB(i,j+1));
                    if(color2.getRed()-color.getRed()>10)
                    {
                        nimg.setRGB(i,j+1,new Color(color2.getRed()-1,color2.getBlue()-1,color2.getGreen()-1).getRGB());
                        nimg.setRGB(i,j,new Color(color.getRed()+1,color.getBlue()+1,color.getGreen()+1).getRGB());
                    }
                }
                if(j-1>-1)
                {
                    Color color2 = new Color(img.getRGB(i,j-1));
                    if(color2.getRed()-color.getRed()>10)
                    {
                        nimg.setRGB(i,j-1,new Color(color2.getRed()-1,color2.getBlue()-1,color2.getGreen()-1).getRGB());
                        nimg.setRGB(i,j,new Color(color.getRed()+1,color.getBlue()+1,color.getGreen()+1).getRGB());
                    }
                }

            }

        }

        ImageIO.write(nimg,"png",new File(out));

    }

    private static Color hex(String hex)
    {
        int resultRed = Integer.valueOf(hex.substring(0, 2), 16);
        int resultGreen = Integer.valueOf(hex.substring(2, 4), 16);
        int resultBlue = Integer.valueOf(hex.substring(4, 6), 16);
        return new Color(resultRed,resultGreen,resultBlue);
    }
}
