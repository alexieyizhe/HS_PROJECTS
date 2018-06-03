/*This is a JFrame that contains the splash screen of the program. It is shown before other screen in the game.
 *The user can click anywhere on the screen in order to progress past this screen.
 */

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;


public class SplashScreen extends JFrame implements MouseListener{
	Image splashimg;
    MainMenu mainmenu;

    public SplashScreen (MainMenu menu){
		super ("Kanye Quest");
		setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		setLayout(null);
		mainmenu = menu;
		setSize(1500, 1000);
		setVisible(true);
		setResizable(false);

		try{
			splashimg = ImageIO.read(getClass().getResourceAsStream("/resources/images/splashscreen.jpg"));
		}catch(IOException e){}


		addMouseListener(this);
		repaint();
    }

    // ------------ MouseListener ------------------------------------------
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mousePressed(MouseEvent e){}
    public void mouseClicked(MouseEvent e){
    	setVisible(false);

		mainmenu.setVisible(true);





    }

    public void paint(Graphics g){
    	g.drawImage(splashimg, 0, 0, this);
    }
}
