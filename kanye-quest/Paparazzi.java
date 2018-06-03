//Paparazzi.java
/*Prushoth Vivekanantha and Alex Xie
This class is responsible for the paparazzi (the enemies who take pictures)

*/



import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.awt.*;
import java.awt.geom.*;

public class Paparazzi extends Enemy {
    private BufferedImage sprite;
    private double ang, returnang;
    private boolean takenpic, capturing, shouldRemove;  ///takenpic isf the paparazzi has taken a photo
                                                          //capturing is if the paparazzi is in the process of taking a picture
                                                         //shouldRemove is if the paparazzi can be removaeds
    private Van home;  //each paparazzi is spawned from a vehicle(van)
    private int capturetime, speed;   //capture time is the time it takes to take a picture and speed is how fast it moves

    public Paparazzi(double x, double y, int hp, Van home) {
        super(x, y, hp);
        coords[0] = x;
        coords[1] = y;
        this.home = home;
        shouldRemove = false;
        speed = 2;
    }

    public boolean inRadius(Kanye k){          //returns if the distance between two objects is less than or = 500 which means it can take a picture
        if (Math.hypot(getX() - k.getX(), getY() - k.getY()) <= 500)  {
            return true;
        }
        return false;
    }
    @Override
    public void changeHP(double n){
        if(!capturing){
            hp += n;            ///takes normal damage
        }
        else{
            hp += n / 2; // give resistance to dmg when taking picture (stationary)
        }
    }


    public void move(BufferedImage mapmask, Kanye k, ArrayList<Paparazzi> plist, ArrayList<Fan> flist, ArrayList<Police> polist){
        //moving towards player
        double dx = k.getX() - coords[0];   //deltax
        double dy = k.getY() - coords[1];   //deltay
        double tmpang = Math.atan2(dy, dx);  //angle between two
        if (takenpic) { //angle is no longer towards player, is towards van
            tmpang = Math.atan2(home.getY() - coords[1], home.getX() - coords[0]);
        }

        ang = tmpang;
        double tmpx = speed * Math.cos(tmpang);  //speed vectors
        double tmpy = speed * Math.sin(tmpang);
        double dist = Math.max(1, Math.hypot(dx, dy));  //checks the case if the distance is 1
        double d2 = Math.pow(dist, 2);
        tmpx -= 130 * dx / d2;
        tmpy -= 130 * dy / d2;

        //paparazzi cannot take pictures through walls if that is possible to fix
        for (Paparazzi p : plist) {
            if (p != this) { //enemy will always collide with itself
                dx = coords[0] - p.getX(); //delta x, total horizontal distance
                dy = coords[1] - p.getY(); //delta y, total vertical distance
                dist = Math.max(1, Math.hypot(dx, dy));
                if (dist < 60) {     //checks the collision between two paparazzi
                    d2 = Math.pow(dist, 2);
                    tmpx += 180 * dx / d2;     //the paparazzi almost bounce off
                    tmpy += 180 * dy / d2;
                }
            }
        }
        //apply the same method to fans and police

        for (Fan f : flist) {
            dx = coords[0] - f.getX(); //delta x, total horizontal distance
            dy = coords[1] - f.getY(); //delta y, total vertical distance
            dist = Math.max(1, Math.hypot(dx, dy));
            if (dist < 60) {
                d2 = Math.pow(dist, 2);
                tmpx += 180 * dx / d2;
                tmpy += 180 * dy / d2;
            }
        }
        for (Police f : polist) {
            dx = coords[0] - f.getX(); //delta x, total horizontal distance
            dy = coords[1] - f.getY(); //delta y, total vertical distance
            dist = Math.max(1, Math.hypot(dx, dy));
            if (dist < 60) {
                d2 = Math.pow(dist, 2);
                tmpx += 180 * dx / d2;
                tmpy += 180 * dy / d2;
            }
        }
        //checks if the paparazzi has reached it's van and if it has taken a picture or if it has died so it can remove it
        if((Math.hypot(home.getX() - coords[0], home.getY() - coords[1]) < 10 && takenpic) || hp <= 0){
            shouldRemove = true;
        }
        //checks if a picture has been taken or if it is not in the range of Kanye to prevent collisoins
        if(takenpic || !inRadius(k)){
            if(isvalidPoint(mapmask, coords[0] + tmpx, coords[1] + tmpy)) {
                //System.out.println("colliding");
                coords[0] += tmpx;
                coords[1] += tmpy;
            }
            capturing = false;
            capturetime = 300;
        }
        else{
            if(capturing){
                capturetime --;             //capturetime decreases
                if(capturetime == 0){     //when the capturing is over, the paparazzi's speed increases
                    takenpic = true;
                    capturing = false;
                    speed = 3;
                }
            }
            else{
                capturetime = 300;
                capturing = true;
            }
        }
    }

    public void draw(Graphics g, int[] offset, BufferedImage[] sprites, KanyePanel k){

        double screenx = coords[0] + offset[0]; //position of object relative to screen, not map
        double screeny = coords[1] + offset[1];
        Graphics2D g2d = (Graphics2D) g;
        AffineTransform oldAT = g2d.getTransform(); //save default transformations
        g2d.translate(screenx, screeny); //move graphics2d object to center of image
        g2d.rotate(ang); //rotate around the center of image
        if(capturing){
            g2d.drawImage(sprites[4], -30, -30, null); //change sprite to sprite of taking picture
        }
        else{
            g2d.drawImage(sprites[3], -30, -30, null); //coords are top left of image
        }
        g2d.setTransform(oldAT); //reset
        if(takenpic){
            //draw amera flash on entire screen, then fade
        }
        else{
            g.setColor(Color.yellow);
            g.fillRect((int)screenx - 30,(int)screeny + 40, (300 - capturetime) / 5, 10); //hp
        }
        //g.drawLine(500, 500, (int)Math.round(1 * Math.cos(ang + Math.toRadians(90))), (int)Math.round(1 * Math.sin(ang + Math.toRadians(90))));
        g.setColor(new Color(34, 139, 34));
        g.fillRect((int)screenx - 50,(int)screeny - 40, (int)hp, 10); //hp


    }

    public boolean picTaken(){return takenpic; }
    //getter methods for shouldRemove and the van
    public boolean checkRemove(){
        return shouldRemove;
    }

    public Van getHome(){
        return home;
    }
    //changing the position of the enemy
    public void changePos(double x, double y){
        coords[0] += x;
        coords[1] += y;
    }
}
