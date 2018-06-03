//Chainsaw.java

//Prushoth Vivekanantha and Alex Xie
// responsible for the chainsaw 

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Chainsaw extends Weapon{

    public Chainsaw(Image sprite, BufferedImage psprite){
        super("chainsaw", sprite, psprite);


    }

    @Override
    public int getFirerate(){return 0;};

    @Override
    public ArrayList<Bullet> shoot(double x, double y, double ang, ArrayList<Bullet> allbuls){
        allbuls.add(new Bullet(x + 50 * Math.cos(ang), y + 50 * Math.sin(ang), ang,  1, 0, 0, 2 ));
        //bullet is in front of player, acts as chainsaw blade which does not move
        return allbuls;
    }




}
