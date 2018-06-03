import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Prushoth on 6/16/2016.
 */
public class GameOver extends JFrame {

    JButton replaybut, menubut, quitbut;
    Image gOverimg;
    MainGame mainm;
    boolean outcome;



    public GameOver (MainGame main, boolean outcome) {
        super("Kanye Quest");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setResizable(false);
        this.outcome = outcome;
        if (outcome) {
            gOverimg = new ImageIcon("resources/images/menu.jpg").getImage();
        }
        else {

        }


        mainm = main;

        setSize(1500, 1000);


        replaybut = new JButton("");
        replaybut.setBounds(375, 600, 175, 175);
        replaybut.setContentAreaFilled(false);
        replaybut.addActionListener(new replay());
        add(replaybut);

        quitbut = new JButton("");
        quitbut.setBounds(600, 600, 300, 300); //position, length and width of button
        quitbut.setContentAreaFilled(false); //make button transparent
        quitbut.addActionListener(new quit()); //check for clicks on button
        add(quitbut); //add button to contentpane

        menubut = new JButton("");
        menubut.setBounds(975, 600, 175, 175);
        menubut.setContentAreaFilled(false);
        menubut.addActionListener(new toMenu());
        add(menubut);
    }
    public void paint(Graphics g) {
        g.drawImage(gOverimg, 0, 0, this);


    }


    class quit implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            System.exit(0);
        }
    }

    class replay implements ActionListener {
        public void actionPerformed(ActionEvent evt) {


        }
    }

    class toMenu implements ActionListener {
        public void actionPerformed(ActionEvent evt) {

        }
    }
}





