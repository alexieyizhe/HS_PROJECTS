/**
 * Created by Alex Xie on 6/2/2016.
 */

import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;

public class Spritesheet {
    private BufferedImage[] allSprites;
    private String name;
    private int numSprites, spriteSizeX, spriteSizeY;

    public Spritesheet(String fileName, int numRows, int numCols, int spriteLength, int spriteWidth){
        BufferedImage spriteSheet = null;

        this.numSprites = numRows * numCols;
        name = fileName;
        spriteSizeX = spriteLength;
        spriteSizeY = spriteWidth;
        allSprites = new BufferedImage[numSprites];
        try{
            spriteSheet = ImageIO.read(new File("/resources/images/sprites/" + fileName + ".png"));
            int i = 0;
            for(int r = 0; r < numRows; r ++){
                for(int c = 0; c < numCols; c ++){
                    allSprites[i] = spriteSheet.getSubimage(c * spriteWidth, r * spriteLength, spriteWidth, spriteLength);
                    i++;
                }

            }

        }catch(IOException e){ System.out.println("There was an error involving " + fileName + ".png."); }

    }

    public int getNumSprites(){
        return numSprites;
    }

    public BufferedImage getSprite(int increment){
        return allSprites[increment];
    }
}
