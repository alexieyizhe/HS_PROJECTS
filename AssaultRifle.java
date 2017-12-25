//Assault Rifle.java
//Prushoth Vivekanantha and Alex Xie
//responsible for the dfault weapon
import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.geom.*;

public class AssaultRifle extends Weapon {
    private int firerate;


    public AssaultRifle(Image icon, BufferedImage psprite, int firerate){
        super("rifle", icon, psprite);
        this.firerate = firerate;
    }

    @Override
    public int getFirerate(){
        return firerate;
    }

    @Override
    public ArrayList<Bullet> shoot(double x, double y, double ang, ArrayList<Bullet> allbuls) {
        allbuls.add(new Bullet(x, y, ang, 1, 10, 1, 5));
        return allbuls;
    }
}