/*
Police.java
Prushoth Vivekanantha and Alex Xie
one of three different types of enemies that  tase Kanye  making him unable to move




 */

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;


public class Police extends Enemy {
    private BufferedImage sprite;
    private double ang, returnang;
    private boolean shooting, shouldRemove;  //flags for removing and shooting
    private Van home; //the home of the police is the  van
    private int firerate,speed, shootcounter;    //firerate determines show often they can shoot
    //shootcounter counts is a counter that goes up as high as possible


    public Police(double x, double y, int hp, Van home) {
        super(x, y, hp);
        coords[0] = x;
        coords[1] = y;
        this.home = home;
        shouldRemove = false;
        speed = 2;
        shooting= false;
        shootcounter =0;
        firerate = 200;

    }

    public void draw(Graphics g,  int[] offset, boolean offscreen, BufferedImage[] sprites, double px, double py){

        double screenx = coords[0] + offset[0]; //position of object relative to screen, not map
        double screeny = coords[1] + offset[1];
        Graphics2D g2d = (Graphics2D) g;
        if(!offscreen){

            AffineTransform oldAT = g2d.getTransform(); //save default transformations
            g2d.translate(screenx, screeny); //move graphics2d object to center of image
            g2d.rotate(ang); //rotate around the center of image
            if(shooting){
                g2d.drawImage(sprites[4], -30, -30, null); //change sprite to sprite of taking picture
            }
            else{
                g2d.drawImage(sprites[3], -30, -30, null); //coords are top left of image
            }

            g2d.setTransform(oldAT); //res


            //g.drawLine(500, 500, (int)Math.round(1 * Math.cos(ang + Math.toRadians(90))), (int)Math.round(1 * Math.sin(ang + Math.toRadians(90))));
        }
        else{ //no need to waste time drawing the object if its offscreen
            //how to draw line at edge of screen closes to obj??
            //g.drawLine(1000, 500, (int)Math.round(1 * Math.cos(ang)), (int)Math.round(1 * Math.sin(ang)));
            g2d.setColor(Color.white);
            double newx, newy;
            double newang =Math.atan2(coords[1] - py, coords[0] - px);

            if (Line2D.linesIntersect(px,py, coords[0], coords[1], 25-offset[0], 25-offset[1], 1475-offset[0], 25 - offset[1]) || Line2D.linesIntersect(px,py, coords[0], coords[1], 25-offset[0], 975-offset[1], 1475-offset[0], 975 - offset[1] )){
                if (py > coords[1]){
                   // System.out.println("drawing  top");
                    newy =   py +offset[1] - 50;
                    newx = newy*1/Math.tan(newang);
                }
                else{
                   // System.out.println("drawing  bottom");

                    newy = -offset[1] + 850 - py;
                    newx = newy*1/Math.tan(newang);
                }
            }
            else{
                if(px < coords[0]){
                    //System.out.println("drawing  rgiht");
                    newx = 1400 + offset[0] - px;
                    newy = newx* Math.tan(newang);
                }
                else{
                    //System.out.println("drawing  left");
                    newx= -offset[0] + 100 - px;
                    newy= newx*Math.tan(newang);
                }

            }
            //System.out.println(newx + " " + newy);
            //System.out.println((int)Math.round(px + newx + offset[0]) + " " + (int)Math.round(py + newy + offset[1]));
           // g.fillOval((int)Math.round(px + newx + offset[0]), (int)Math.round(py + newy + offset[1]), 50, 50);

        }
        g.setColor(new Color(34, 139, 34));
        g.fillRect((int)screenx - 50,(int)screeny - 40, (int)hp, 10); //hp


    }
    //check if in 70 pixel radius as kanye
    public boolean inRadius(Kanye k){
        return (Math.hypot(getX() - k.getX(), getY() - k.getY()) <= 500);

    }
    //
    @Override
    public void changeHP(double n){
        if(!shooting){
            hp += n;
        }
        else{
            hp += n / 2; // give resistance to dmg when shooting (stationary)
        }
    }


    public void move(Kanye k, ArrayList<Paparazzi> plist, ArrayList<Fan> flist, ArrayList<Police> polist, ArrayList<Bullet> allbuls){
        //moving towards player
        //exact same as fans and paparazzi
        double dx = k.getX() - coords[0];
        double dy = k.getY() - coords[1];
        double tmpang = Math.atan2(dy, dx);


        ang = tmpang;
        double tmpx = speed * Math.cos(tmpang);
        double tmpy = speed * Math.sin(tmpang);
        double dist = Math.max(1, Math.hypot(dx, dy));
        double d2 = Math.pow(dist, 2);
        tmpx -= 130 * dx / d2;
        tmpy -= 130 * dy / d2;

        //paparazzi cannot take pictures through walls if that is possible to fix
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
            if(p!= this) {
                dx = coords[0] - p.getX(); //delta x, total horizontal distance
                dy = coords[1] - p.getY(); //delta y, total vertical distance
                dist = Math.max(1, Math.hypot(dx, dy));
                if (dist < 60) {
                    d2 = Math.pow(dist, 2);
                    tmpx += 180 * dx / d2;
                    tmpy += 180 * dy / d2;
                }
            }

        }

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

        //remove if hp is less than 0
        if(hp <= 0){
            shouldRemove = true;
        }
        //shootcounter increases
        shootcounter++;
        if(shootcounter++ == Integer.MAX_VALUE){ //integer will not overflow if game runs too long
            shootcounter = 0;
        }
        if(!inRadius(k)){

            coords[0] += tmpx;
            coords[1] += tmpy;
            shooting = false;  //shooting is false if it is not nearkanye

        }
        else{

            if (shootcounter > firerate) {    //shootcounter has to be greater than firerate to shoot
                shooting = true;
                if(!k.getTased()){              //can't shoot when kanye is tased because then kanye can't move
                    shoot(allbuls, sprite);
                }
                shootcounter = 0;
            }




        }


    }
    //shoot a single bullet
    public void shoot(ArrayList<Bullet> allbuls, BufferedImage sprite){
        allbuls.add(new Bullet(coords[0],coords[1], ang, 5, 30, 1, 3));



    }

    //getter methods for shouldRemove and home
    public boolean checkRemove(){
        return shouldRemove;
    }

    public Van getHome(){
        return home;
    }

    public void changePos(double x, double y){
        coords[0] += x;
        coords[1] += y;
    }

}
