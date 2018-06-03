import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.geom.*;
import javax.imageio.*;
import java.io.*;

public abstract class Enemy {
    public double[] coords = new double[2];
    public double ang;
    public double hp, atkcounter;
    private boolean onFire;

    public Enemy(double x, double y, int hp){
        coords[0] = x;
        coords[1] = y;
        this.hp = hp;
        ang = 0;
        atkcounter = 50;
        onFire = false;

    }
    public double getX(){
        return coords [0];
    }

    public double getY(){
        return coords[1];
    }

    public double getHP(){
        return hp;
    }

    public double getAng(){
        return ang;
    }

    public void changeHP(double n){
        hp += n;
    }

    public void updateSprite(){
    }

    public void setonFire(boolean tf){
        onFire = tf;
    }

    public boolean isonFire(){
        return onFire;
    }

    public boolean collide(double x, double y, int dist){
        if(Math.hypot(coords[0]  - x, coords[1]  - y) < dist){
            return true;
        }
        return false;
    }

    public boolean isvalidPoint(BufferedImage mask, double x, double y){
        if(x >= 0 && x < mask.getWidth() && y >= 0 && y < mask.getHeight()){
            return !(new Color(mask.getRGB((int)Math.round(x), (int)Math.round(y))).equals(Color.black));
        }
        return true;
    }
}
