
/*
Pistol.java
Prushoth Vivekanantha and Alex Xie
This is the class that deals for the pistol
It automatically kills the enemies
 */
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.awt.*;
import java.awt.geom.*;
public class Pistol extends Weapon{
    private int firerate;
    public Pistol(Image sprite, BufferedImage psprite,  int firerate){
        super("pistol", sprite, psprite);
        this.firerate = firerate;

    }
    //shoots  (adds new bullet to buullet list)
    @Override
    public ArrayList<Bullet> shoot(double x , double y, double ang , ArrayList<Bullet> allbuls){
        allbuls.add(new Bullet(x,y, ang, 1, 10, 1, 1));
        return allbuls;
    }
    @Override

    public int getFirerate(){
        return firerate;
    }

}
