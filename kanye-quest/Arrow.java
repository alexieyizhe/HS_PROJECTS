/*
Arrow.java
Prushoth Vivekanantha
Extension of weapon that shoots arrows
The damage dealt is due how long it is held for
 */

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.awt.*;
import java.awt.geom.*;
public class Arrow extends Weapon {
    private String weptype;
    private int drawTime;
    private boolean hasReleased = false;  //boolean to check if the arrow has been shot or not

    public Arrow(Image sprite, BufferedImage psprite){
        super("arrow", sprite, psprite);
        drawTime = 0;
    }
    //draws the arrow
    public void drawBow(){
        drawTime ++;  //drawtime increases
        if(drawTime >= 300){   //the maxium drawtime is 300  making it forced to be released
            hasReleased = true;
        }
    }
    //the release of an arrow
    public void release(){
        hasReleased = true;
    }
    //returning isReleased
    public boolean isReleased(){
        return hasReleased;
    }

    @Override
    //overriding shoot because it is different for each weapon
    public ArrayList<Bullet> shoot(double x,double y, double ang, ArrayList<Bullet> allbuls){
        if (allbuls.size() < 10){
            //make sure the size of the bullet list is less than 10

            allbuls.add(new Bullet(x,y, ang, 4, drawTime/10 + 10, 0.95, drawTime / 3));
            //adding arrow bullets with a negative acceleration
            hasReleased = false;
            //sets hasReleased to false again
            //reset drawTime
            drawTime = 0;

        }
        return allbuls;
    }
    //getter method for drawTime and firerate
    public int getdrawTime(){
        return drawTime;
    }

    @Override
    public int getFirerate(){
        return 0;
    }
    //draws the bar for drawTime with darkness of colour increasing
    public void draw(Graphics g, Kanye k, int[] offset){

        g.setColor(new Color(drawTime/3 * -155/100 + 255, 0,0));
        g.fillRect((int)Math.round(k.getX() + offset[0]), (int)Math.round(k.getY()+offset[1])+30, drawTime/3 , 10);
    }



}
