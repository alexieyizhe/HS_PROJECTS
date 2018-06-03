/*
Weapon.java
Prushoth Vivekanantha
abstract class for weapons
 */


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.awt.*;
import java.awt.geom.*;

public abstract class Weapon {
    public Image sprite;
    public BufferedImage playersprite;
    public String weptype;

    public Weapon(String weptype,  Image sprite, BufferedImage playersprite){
        this.sprite = sprite;
        this.playersprite = playersprite;
        this.weptype = weptype;
    }
    //getter methods for sprite, playersprite, and firerate
    public Image getSprite(){
        return sprite;
    }

    public BufferedImage getPlayersprite(){ return playersprite; }

    public abstract int getFirerate();
    //shooting a bullet
    public abstract ArrayList<Bullet> shoot(double x, double y, double ang, ArrayList<Bullet> allbuls);


    public String toString(){ return weptype; }


}
