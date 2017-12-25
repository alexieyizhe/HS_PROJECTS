/*
Fan.java
Prushoth Vivekanantha and Alex Xie
This is the class that deals with the fans which is one of the three enemies
 */
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.awt.*;
import java.awt.image.BufferedImage;
public class Fan extends Enemy{
    private double ang;
    private BufferedImage sprite;
    private int atkspeed; //how fast it attacks
    private boolean hit;

    public Fan(double x, double y, int hp, int atkspeed, BufferedImage sprite){
        super(x, y, hp);
        this.sprite = sprite;
        coords[0] = x;
        coords[1] = y;
        this.sprite = sprite;
        this.atkspeed = atkspeed;

        hit = false;

    }
    ///deals with the movment and collision of the fans
    public void move(BufferedImage mapmask, Kanye k, ArrayList<Fan> flist, ArrayList<Paparazzi> plist, ArrayList<Police> polist){
        //tbe exact same movement algorithm is used in paparazzi and police
        double dx = k.getX() - coords[0]; //delta x, total horizontal distance
        double dy = k.getY() - coords[1]; //delta y, total vertical distance
        double tmpang = Math.atan2(dy, dx);
        double tmpx = 2 * Math.cos(tmpang);
        double tmpy = 2 * Math.sin(tmpang);

        //moving towards player
        double dist = Math.max(1, Math.hypot(dx, dy));
        double d2 = Math.pow(dist, 2);
        tmpx -= 130 * dx / d2;
        tmpy -= 130 * dy / d2;

        for (Fan f : flist) {
            if (f != this) { //enemy will always collide with itself
                dx = coords[0] - f.getX(); //delta x, total horizontal distance
                dy = coords[1] - f.getY(); //delta y, total vertical distance
                dist = Math.max(1, Math.hypot(dx, dy));
                if (dist < 60) {
                    d2 = Math.pow(dist, 2);
                    tmpx += 180 * dx / d2;
                    tmpy += 180 * dy / d2;
                }
            }
        }

        for (Paparazzi p : plist) {
            dx = coords[0] - p.getX(); //delta x, total horizontal distance
            dy = coords[1] - p.getY(); //delta y, total vertical distance
            dist = Math.max(1, Math.hypot(dx, dy));
            if (dist < 60) {
                d2 = Math.pow(dist, 2);
                tmpx += 180 * dx / d2;
                tmpy += 180 * dy / d2;
            }
        }
        for (Police p : polist) {
            dx = coords[0] - p.getX(); //delta x, total horizontal distance
            dy = coords[1] - p.getY(); //delta y, total vertical distance
            dist = Math.max(1, Math.hypot(dx, dy));
            if (dist < 60) {
                d2 = Math.pow(dist, 2);
                tmpx += 180 * dx / d2;
                tmpy += 180 * dy / d2;
            }
        }
        //checks the collision between the fans and the map
        if(isvalidPoint(mapmask, coords[0] + tmpx, coords[1] + tmpy)) {
            coords[0] += tmpx;
            coords[1] += tmpy;
        }
        ang = tmpang;
    }
    //responsible for attacking kanye
    public boolean attack(Kanye k){
        if (atkcounter >= atkspeed && Math.hypot(coords[0] - k.getX(), coords[1] - k.getY()) < 70){ //hecks if the rate of attacking is viable and it is in 70 pixels radius of the player
            k.changeHP(-3); //reduce the health of kanye by 3
            atkcounter = 0; //reset attackcounter
            return true;
        }
        atkcounter ++; //attackcounter increases
        return false;
    }
    //responsible for drawing
    public void draw(Graphics g, KanyePanel k, int[] offset){
        double screenx = coords[0] + offset[0]; //position of object relative to screen, not map
        double screeny = coords[1] + offset[1];

        //is there a way to simplify this shit
        Graphics2D g2d = (Graphics2D) g;
        AffineTransform oldAT = g2d.getTransform(); //save default transformations
        g2d.translate(screenx, screeny); //move graphics2d object to center of image
        g2d.rotate(ang); //rotate around the center of image
        g2d.drawImage(sprite, -30, -30, null); //coords are top left of image
        g2d.setTransform(oldAT); //reset

        g.setColor(new Color((int)(4 * (50 - hp) + 20), (int)(5 * hp), 64, 175));
        g.fillRect((int)screenx - 50,(int)screeny - 40, (int)hp, 10); //hp
    }


}