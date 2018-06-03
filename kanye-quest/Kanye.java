/*
Kanye.java
Prushoth Vivekanantha and Alex Xie

This is the class that deals with everything that has to do with the player

 */



import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Kanye {

    private double[] coords = new double[2];
    private double ang;
    private Weapon curwep;  //the current weapon of the player
    private int speed, hp;    
    private boolean shooting;
    private boolean invincible;
    private Powerup[] powerupList;  //list of powerups 
    private boolean tased;   //boolean for if kanye is tased or not
    private Powerup speedBoost, hpBoost;           //powerup for speed and health
    private int tasecounter;  //counter for how long kanye is tased for

    public Kanye(double x, double y, Weapon curwep) {
        this.curwep = curwep;
        coords[0] = x;
        coords[1] = y;
        speed = 5;
        hp = 100;
        powerupList = new Powerup[2];
        for (int i = 0; i < 2; i++) {
            powerupList[i] = null;              
        }
        tasecounter = 100;  //tasecounter is 100 initially

    }
    //setter and getter methods for shooting
    public void setShooting(boolean s) {
        shooting = s;
    }

    public boolean isShooting() {
        return shooting;
    }
    //colliding with differend objects by making sure the distance between the two is less than a certain distance
    public boolean collide(double x, double y, int dist) {
        return (Math.hypot(coords[0] - x, coords[1] - y) < dist);

    }
    //picking up a powerup
    public void pickup(Powerup newp) {
        if (newp.isType("yeezys")) {  //yeezys give speed
            speedBoost = newp;
            return;
        }
        hpBoost = newp;  

    }
    //override pickup to increase health by  10 but can never exceed 100
    public void pickup (Health h){
        if(hp >=100){
            hp = 100;
        }
        hp += 10;
    }
    //picking up a weapon setting the current weapon to the picked up one
    public void pickup(Weapon w){
        curwep = w;
    }

    //getter methods for hp, x,y, ang, weapon, speed
    public int getHP() {
        return hp;
    }

    public double getX() {
        return coords[0];
    }

    public double getY() {
        return coords[1];
    }

    public double getAng() {
        return ang;
    }

    public Weapon getWep() {
        return curwep;
    }

    public int getSpeed() {
        return speed;
    }
    //setter method for speed
    public void setSpeed(int n) {
        speed = n;
    }
    //setter methods for tased and angle
    public void changeAng(double a) {
        ang = a;
    }
    public void setTased(boolean tf){
        tased = tf;
    }
    //getter method for tased
    public boolean getTased(){
        return tased;
    }
    //changing the hp
    public void changeHP(int n) {
        if (n < 0 && hpBoost != null) {   //reduce damage if kanye is wearing a vest
            n /= 2;
        }

        hp += n;   //changing the hp
      
        hp = (hp < 0) ? 0 : hp;             
        hp = (hp > 100) ? 100 : hp;
    }

    public void move(double displacedx, double displacedy) {
        
        if (!tased) {   //makes sure kanye is not tased
            coords[0] += displacedx * speed;
            coords[1] += displacedy * speed;
        }
    }

    //updates the player
    public void updatePlayer() {
        if (tased && tasecounter > 0){   ///when kanye is tased the counter for taser ticks down
            tasecounter--;
        }
        if (tasecounter <=0){   //he becomes untased when tasecounter reaches 0
            tased = false;
            tasecounter = 100;
        }
        if (speedBoost != null) {          //if kanye has the speed powerup 
            speedBoost.update();
            if (speedBoost.ranOut()) {  //once it runs out make the powerup null
                speedBoost = null;
            }
        }

        if (hpBoost != null) {  //the same goes for the vest powerup
            hpBoost.update();
            if (hpBoost.ranOut()) {
                hpBoost = null;
            }
        }

        speed = speedBoost == null ? 5 : 7;    //increases when it has the powerup

    }

    public void draw(Graphics g, KanyePanel k, int[] offset) {

        //is there a way to simplify this shit
        Graphics2D g2d = (Graphics2D) g;
        AffineTransform oldAT = g2d.getTransform(); //save default transformations
        g2d.translate(coords[0] + offset[0], coords[1] + offset[1]); //move graphics2d object to center of image
        if(!tased){
            g2d.rotate(ang); //rotate around the center of image
        }
        g2d.drawImage(curwep.getPlayersprite(), -40, -40, k); //codords are top left of image, gun sticks out 42 pixels
        g2d.setTransform(oldAT); //reset
    }
    //getter method for the vest
    public Powerup gethpPower() {
        return hpBoost;
    }
    //getter methodfor the speed
    public Powerup getspeedPower() {
        return speedBoost;
    }
}