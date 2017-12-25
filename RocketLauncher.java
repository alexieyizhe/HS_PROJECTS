/*
RocketLauncher.java
Prushoth Vivekanantha and Alex Xie
This is the weapon that shoots rockets


 */
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.awt.*;
import java.awt.geom.*;
public class RocketLauncher extends Weapon{
    private int firerate;

    public RocketLauncher(Image icon, BufferedImage psprite, int firerate){
        super("rocket launcher", icon, psprite);
        this.firerate = firerate;
    }
    @Override
    public int getFirerate(){
        return firerate;
    }

    @Override
    public ArrayList<Bullet> shoot(double x, double y, double ang, ArrayList<Bullet> allbuls){
        allbuls.add(new Bullet(x,y, ang,3, 10, 1, 30)); //adds a rocket bullet

        return allbuls;
    }


}
