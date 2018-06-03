/*Flamethrower.java
Prushoth Vivekanantha and Alex Xie
This is a weapon that generates flames and sets enemies on fire





 */
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.awt.*;
import java.awt.geom.*;

public class Flamethrower extends Weapon {
    private int range;
    private Random rn = new Random();
    private int firerate;
    public Flamethrower(Image icon, BufferedImage psprite, int range, int firerate){
        super("flamethrower", icon, psprite);
        this.range = range;
        this.firerate = firerate;



    }
    @Override
    public ArrayList<Bullet> shoot(double x, double y, double ang, ArrayList<Bullet> allbuls){
        for(int i = 0; i < 30; i++){


            double randP = Math.PI/16 *(rn.nextDouble() * 2 - 1);  //randP is the range between -pi/16 and pi/16
            //System.out.println(randP);
            int randSpeed = rn.nextInt(30) + 10;  //generates a random speed between 10 and 39
            Bullet b = new Bullet(x, y, ang + randP, 2, randSpeed, 0.9, .05);  //0.05 is the damage and 0.9  is the acceleration meaning it slows down
            if (randP >0) {   //since the median is 0 between the range therefore if it is above 0 then it is moving left
                b.setDirection(true);
            }
            allbuls.add(b);
        }
        return allbuls;
    }

    @Override
    public int getFirerate() {
        return firerate;
    }

}