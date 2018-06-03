/*
Health.java
Prushoth Vivekanantha and Alex Xie
This is a pickup that increases the health of kanye by ten
 */

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.awt.*;
import java.awt.geom.*;
public class Health {
    private double x,y;
    private Image sprite;
    private BufferedImage pointer;

    public Health(double x, double y, Image sprite, BufferedImage pointer){
        this.x= x;
        this.y= y;
        this.sprite= sprite;
        this.pointer = pointer;
    }
    //draws the gif
    public void draw(Graphics2D g2d, KanyePanel k, int[] offset, int px, int py, boolean offscreen) {
        if (offscreen) {
            double newx, newy;
            double newang = Math.atan2(x - py, y - px);
            //create a line with y=0 and y = 1000 and the line between the paparazzi and the player
            //checks if they intersect
            if (Line2D.linesIntersect(px,py, x, y, 25 - offset[0], 25-offset[1], 1475-offset[0], 25 - offset[1]) || Line2D.linesIntersect(px,py, x, y, 25-offset[0], 975-offset[1], 1475-offset[0], 975 - offset[1] )){
                //however they can intersect twice from the bottom and the top so
                //check if the player's y coordinate is less than the one of the paparazzi
                if (py > y){
                    newy =   -py -offset[1] +  50;  //the y coordinate of the pointer relative to the player is known
                    newx = newy*1/Math.tan(newang); //from this since the y value is known and the angle is known then the x value is the y value * cotangent(ang)
                }
                //if the player's y coordinate less than the one of the paparazzi's
                else{
                    //System.out.println("drawing  bottom");

                    newy = -offset[1] + 850 - py;  //the y coordinate is  known so using trig, the x  coordinate can be found
                    newx = newy*1/Math.tan(newang);
                }
            }
            else{ //creates lines with x = 0 and x = 1500
                if(px < x){  //checks if the enemy is on the left
                    newx = 1400 - offset[0] -px; //the x coordinate can be found using subtraction making it 100 coordinates to the right of x= 0
                    newy = newx* Math.tan(newang);//use this to find the y coordinate similarly to the one above
                }
                else{
                    //System.out.println("drawing  left");
                    newx= -offset[0] + 100 - px;  //offset[0] - 100 +px;
                    newy= newx*Math.tan(newang);
                }

            }
            //System.out.println(newx + " " + newy);
            //System.out.println((int)Math.round(px + newx + offset[0]) + " " + (int)Math.round(py + newy + offset[1]));
            // g2d.fillOval((int)Math.round(px + newx + offset[0]), (int)Math.round(py + newy + offset[1]), 50, 50);

            // g2d.translate(newx, newy);
            //g2d.rotate(newang);Graphics2D g2d = (Graphics2D) g;
            AffineTransform oldAT = g2d.getTransform(); //save default transformations
            g2d.translate((int)Math.round(px + newx + offset[0]), (int)Math.round(py + newy + offset[1])); //move graphics2d object to center of image
            g2d.rotate(Math.atan2(y - py, x - px)); //rotate around the center of image
            g2d.drawImage(pointer, -50, -50, k); //codords are top left of image, gun sticks out 42 pixels
            g2d.setTransform(oldAT); //reset
            //g2d.setTransform(oldAt);
        } else {
            g2d.drawImage(sprite, (int) Math.round(x + offset[0]), (int) Math.round(y + offset[1]), k);
        }
    }
    //getter methods for x and y coordinates
    public double getX(){
        return x;
    }
    public double getY(){
        return y;
    }


}
