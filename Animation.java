/**
 * Created by Alex Xie on 6/2/2016.
 */

import java.io.*;
import javax.swing.*;
import java.util.*;
import java.awt.Image;

public class Animation {
    private double xpos, ypos;
    private int animateLength;
    private Image gif;
    private String name;
    private boolean loop, finishAnimate;

    public Animation(String name, double x, double y, int animateLength, Image gif){
        this.name = name; //what the animation is used for
        xpos = x;
        ypos = y;
        this.animateLength = animateLength; //how long each image is shown on screen
        this.gif = gif;
        finishAnimate = false;
        loop = false;
        if(animateLength == -1){
            loop = true;
        }
    }

    public Image update(){
        animateLength --;
        if(animateLength <= 0 && !loop){
            finishAnimate = true;
        }
        return gif;
    }

    public String getName(){
        return name;
    }

    public double getX(){
        return xpos;
    }

    public double getY(){
        return ypos;
    }

    public int getLength(){
        return animateLength;
    }

    public boolean isFinished(){
        return finishAnimate;
    }

    public boolean isLooping(){
        return loop;
    }

    public Image getImage(){
        return gif;
    }
}
