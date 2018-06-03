
/*Shotgun.java
Prushoth Vivekanantha and Alex Xie
This is the class that is responsible for shotgun
the gun shoots three bullets
 */

import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;

public class Shotgun extends Weapon   { //extension of weapons
    private int firerate;

    public Shotgun(Image icon, BufferedImage psprite, int firerate){
        super("shotgun", icon, psprite);
        this.firerate = firerate;
    }

    @Override
    public int getFirerate(){
        return firerate;
    }

    @Override
    public ArrayList<Bullet> shoot(double x, double y, double ang, ArrayList<Bullet> allbuls){
        for(int i = -2; i <= 2; i ++) {   //sets the range from -2 to 2 so it splits
            allbuls.add(new Bullet(x, y, ang + Math.toRadians(i * 5), 1, 10, 1, 25));

        }
        return allbuls;
    }

}
