/*This is a JFrame that contains the buttons and images that make up the main menu screen.
 *This screen is shown after the user proceeds past the splash screen and contains 5 buttons:
 *	Play, which starts the main game
 *	Instructions, which shows the user how to play the game
 *	Settings, which allows the user to change how many players will be playing the game
 *	Credits, which displays who made this game
 *	Quit, which exits the program
 *When a button is clicked, the specific function is activated.
 */

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.io.*;


public class MainMenu extends JFrame{
    JButton startbut, instructbut, settingbut, quitbut, creditbut;
    ImageIcon startmenu;
    MainGame mainm;

    public MainMenu (MainGame main){
        super ("Kanye Quest");
        setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setResizable(false);
        try{
            startmenu = new ImageIcon(ImageIO.read(getClass().getResourceAsStream("resources/images/menu.jpg")));
        }catch(IOException e){System.out.println("error");}

        setContentPane(new JLabel(startmenu)); //setting background to mainbackground


        mainm = main;

        setSize(1500, 1000);

        instructbut = new JButton("");
        instructbut.setBounds(365, 585, 175, 175);
        instructbut.setContentAreaFilled(false);
        instructbut.setBorderPainted(false);
        instructbut.addActionListener(new showInstructions());
        add(instructbut);

        startbut = new JButton("");
        startbut.setBounds(595, 575, 300, 300); //position, length and width of button
        startbut.setContentAreaFilled(false); //make button transparent
        startbut.setBorderPainted(false);
        startbut.addActionListener(new startGame()); //check for clicks on button
        add(startbut); //add button to s

        settingbut = new JButton("");
        settingbut.setBounds(970, 580, 175, 175);
        settingbut.setContentAreaFilled(false);
        settingbut.setBorderPainted(false);
        settingbut.addActionListener(new showSettings());
        add(settingbut);

        quitbut = new JButton("");
        quitbut.setBounds(1270, 765, 150, 150);
        quitbut.setContentAreaFilled(false);
        quitbut.setBorderPainted(false);
        quitbut.addActionListener(new quitGame());
        add(quitbut);

    }

    class startGame implements ActionListener{
        public void actionPerformed(ActionEvent evt){
            setVisible(false);
            KanyePanel game = new KanyePanel(mainm, 1); //this is the only way to update the settings and config (initialize gamepanel after changing settings)
            mainm.setVisible(true);//otherwise you must restart to apply changes to player number (normal way initializes gamepanel right after initializing startmenu)
            mainm.addPanel(game); //change main copy of TronLightcycles to new one initialized here
            mainm.add(game);	//add gamepanel to the contentpane of maingame
        }
    }

    class showInstructions implements ActionListener{ //show popup of instruction page
        public void actionPerformed(ActionEvent evt){
            JLabel instrucpic = new JLabel(new ImageIcon("resources/images/instructions.jpg"));
            JOptionPane.showOptionDialog(null, instrucpic,"Instructions", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{}, null);
        }
    }

    class showSettings implements ActionListener{ //settings for adjusting number of players
        public void actionPerformed(ActionEvent evt){
            Object[] options = {"Kanye","Kim", "North", "Kylie"};
            int n = JOptionPane.showOptionDialog(null, "Who would you like to play as?", "Options", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
            if(n >= 0){
                try{
                    PrintWriter config = new PrintWriter(new BufferedWriter (new FileWriter ("resources/configs/config.txt", false))); //open config file
                    config.close(); //close file
                }
                catch(IOException e){
                    System.out.println("There was an error reading the config file. The config file may not exist in this folder.\nError Message: " + e.getMessage());
                }
            }
        }
    }

    class quitGame implements ActionListener{ //close the game completely
        public void actionPerformed(ActionEvent evt){
            System.exit(0);
        }
    }
}
