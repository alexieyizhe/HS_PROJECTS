/*
TripMine.java
Prushoth Vivekanantha
When the player presses e, tripmines spawn and if the enemies go over it , they explode
 */
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.awt.*;
import java.awt.geom.*;

public class TripMine {
    private double x, y;
    private BufferedImage sprite;
    private int damage, range;

    public TripMine(double x, double y, BufferedImage sprite) {
        this.x = x;
        this.y = y;

        this.sprite = sprite;

    }

    public double getX(){
        return x;
    }
    public double getY(){
        return y;
    }

    public void changePos(double x, double y){
        this.x += x;
        this.y += y;
    }
    //spawns a new explosion
    public Explosion blowUP(){
        return new Explosion(x, y, 40, 75);
    }
    //drawing the tripsprite
    public void draw(Graphics g, KanyePanel k, int[] offset){
        g.drawImage(sprite, (int)Math.round(x) + offset[0], (int)Math.round(y) + offset[1], k);
        //g.drawImage(sprite, -40 , -40, k);
    }
}



