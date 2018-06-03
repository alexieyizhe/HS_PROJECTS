/* Bullet.java
Prushoth Vivekanantha and Alex Xie
This is the class that deals with all the bullets shot by either Kanye or the police
Has methods that is responsible for the movement, collision, and drawing of the bullet




 */
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.awt.*;
import java.awt.geom.*;

public class Bullet{

    //there are 5 types of bullets
    private final int REGULAR = 1; //assault rifle, shotgun
    private final int FLAME = 2; //flamethroweer
    private final int ROCKET = 3;  //rocketlauncher
    private final int ARROW = 4; //bow and arrow
    private final int TASER = 5;//not used by Kanye, only by police

    private double damage;
    private double ang;
    private double[] coords = new double[2];  //the coordinates of the bullet
    private BufferedImage sprite;
    private Ellipse2D bounds; //change later to image collision
    private int bulletType;
    private double speed, accel;
    private boolean movingLeft; //if the bullet is moving left


    public Bullet(double x, double y, double ang, int bultype, double speed, double accel, double damage){
        coords[0] = x;
        coords[1] = y;
        this.ang = ang;

        bulletType = bultype;
        this.speed= speed;
        movingLeft = false;
        this.damage = damage;
        this.accel = accel;


    }
    public double getAng(){
        return ang;
    }
    //method to make the bullet move left or right
    //true means the bullet is moving left anddvice versa
    public void setDirection(boolean tf){
        movingLeft = tf;
    }
    public int getType(){
        return bulletType;
    }
    public void move(){
        if (bulletType == FLAME){   //bullets in flamethrower deviate slightly from their angle
           if (movingLeft){
               ang *= 1.002;  //if left the angle is increased gradually
           }

           else{
               ang *= 0.999;  //if right the angle is decreased
           }
        }
        speed *= accel;    //speed either decreases or increases
        coords[0] += speed * Math.cos(ang);   //bullet moves
        coords[1] += speed * Math.sin(ang);

    }
    //getter methods for the position (x and y respectively) and speed
    public double getX(){
        return coords[0];
    }
    public double getY(){
        return coords[1];
    }
    public double getSpeed(){ return speed; }

    //checking if the bullet collides with anything
    public boolean collide(double x, double y){
        return(x == coords[0] && y == coords[1]);  //the bullet is only around one pixel or the tip of it is

    }
    //drawing the bullet
    public void draw(Graphics2D g2, KanyePanel k, int[] offset){
        if(bulletType == FLAME){                   //the flamethrower's bullets are different shades of orange and are circular
            //System.out.println((Math.random() * 50 + 200));
            g2.setColor(new Color(255, (int)(Math.random() * 100 + 90), 59, (int)(Math.random() * 50 + 200)));
            g2.fillOval((int)Math.round(coords[0]) - 5 + offset[0], (int)Math.round(coords[1]) - 5 + offset[1], 10, 10);
        }
        else if(bulletType == TASER){         //the taser is oval and randomly coloured
            //System.out.println((Math.random() * 50 + 200));
            g2.setColor(new Color((int)(Math.random() * 100 + 100), 255, 255));
            g2.fillOval((int)Math.round(coords[0]) - 5 + offset[0], (int)Math.round(coords[1]) - 5 + offset[1], 10, 10);
        }
        else if(bulletType == ARROW){
            g2.setColor(new Color(255, 255, 255, 200));
            g2.setStroke(new BasicStroke(7));
            g2.drawLine((int)Math.round(coords[0]) + offset[0], (int)Math.round(coords[1]) + offset[1], (int)Math.round(coords[0] - 10 * Math.cos(ang)) + offset[0], (int)Math.round(coords[1] - 10 * Math.sin(ang)) + offset[1]);
            g2.setColor(new Color(255, 0, 0, 200));
            g2.drawRect((int)Math.round(coords[0]) + offset[0], (int)Math.round(coords[1]) + offset[1], 8, 8);
        }
        else{ //every other bullet type
            g2.setColor(new Color(3, 3, 3, 200));
            g2.setStroke(new BasicStroke(7));
            g2.drawLine((int)Math.round(coords[0]) + offset[0], (int)Math.round(coords[1]) + offset[1], (int)Math.round(coords[0] - 7 * Math.cos(ang)) + offset[0], (int)Math.round(coords[1] - 7 * Math.sin(ang)) + offset[1]);
        }
        //all the other bullets are white lines



        //replace this with image and proper rotation later on
        //g.drawImage(sprite, (int)Math.round(coords[0]) - 5 + offset[0], (int)Math.round(coords[1]) - 5 + offset[1], k);
    }
    //getter image for damage
    public double getDamage(){return damage;}

}
