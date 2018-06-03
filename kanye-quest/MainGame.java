/*`
MainGame.java
Prushoth Vivekanantha and Alex Xie


Kanye Quest is a top down shooter game where the objective is to kill enough paparazzi, police ,and fans to eventually get to the helicopter and escape
 */

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.util.*;
import java.util.Random;
import javax.swing.*;
import java.awt.event.*;
import javax.imageio.*;
import java.io.*;
import java.awt.MouseInfo;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class MainGame extends JFrame implements ActionListener{
	private javax.swing.Timer gameTimer;
	private KanyePanel game;
    private MainMenu menu;
    private SplashScreen splash;

	public MainGame(){
		super("Kanye Quest");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1500, 1000);

		setResizable(false);
        setVisible(false);



        menu = new MainMenu(this);
        splash = new SplashScreen(menu);
        gameTimer = new javax.swing.Timer(10, this);
	}

	public void start(){
		gameTimer.start();
	}

	public void actionPerformed(ActionEvent evt){
      game.repaint();
      game.move();
	}

    public void addPanel(KanyePanel kp){
        game = kp;
    }

	public static void main(String[] args){
		MainGame frame = new MainGame();
	}
}


class KanyePanel extends JPanel implements KeyListener, MouseMotionListener, MouseListener{
    private final int XVAL = 0;
    private final int YVAL = 1;

	private boolean[] keys;
    private int[] offset, displacement; //offset of map for scrolling
    private Weapon[] allweps;
	private ArrayList<Bullet> bullets= new ArrayList<Bullet>(); //arraylist for bullets, fafns, tripmines, , explosions, paparazzi, vans, powerups, police, healthkit
    private ArrayList<Fan> fans = new ArrayList<Fan>();
    private ArrayList<TripMine> tripmines = new ArrayList<TripMine>();
    private ArrayList<Explosion> explosions = new ArrayList<Explosion>();
    private ArrayList<Paparazzi> paparazzi = new ArrayList<Paparazzi>();
    private ArrayList<Van> vanList = new ArrayList<Van>();
    private ArrayList<Powerup> powerList = new ArrayList<Powerup>();
    private ArrayList<Police> policelist = new ArrayList<Police>();
    private Health healthkit;

    private boolean spawnFans, spawnPap, spawnPol;  //booleans for the spawning of fans paparaazzi and police
    private int fanspawnRate, papspawnRate, polspawnRate, enemycount;    //the rates for spawning them
    private ArrayList<int[]> fanspawnLocs = new ArrayList<int[]>();  //locations for spawning the fans, paparazzi, police
    private ArrayList<int[]> papspawnLocs = new ArrayList<int[]>();
    private ArrayList<int[]> polspawnLocs = new ArrayList<int[]>();
    private ArrayList<int[]> weaponlist = new ArrayList<int[]>(); // integer list containing the coordinates and weapon type number
    private ArrayList<double[]> impactlist = new ArrayList<double[]>();  //integer list for impacts
    private ArrayList<Animation> allAnimations = new ArrayList<Animation>();  //array list for animations/

    private MainGame mainFrame;
    private BufferedImage[] kanyepics;
    private BufferedImage[] enemyPics;
    private Random rn = new Random();
	private Kanye player;

	private int mx , my, mainCounter, shootCounter;
    private BufferedImage[] allPointers;  //array of pointers
    private BufferedImage map, minimap,  trippic, papvanPic, polvanPic, vestpic;  //loading the images
    private BufferedImage mapmask;
    private Image explosiongif, healthgif, yeezygif, vestgif;//loading gifs

    private Font hudf, smallerf, smallestf;  //3 types of fonts
    private AudioStream kanyebeats;

	public KanyePanel(MainGame m, int level){
		mainFrame = m;
        addKeyListener(this);
        addMouseMotionListener(this);
        addMouseListener(this);
        setSize(1500, 1000);

        keys = new boolean[KeyEvent.KEY_LAST+1];
        offset = new int[]{0, 0};  //array of offset
        displacement = offset;
        mainCounter = 0;  //maincounter  , shootcounter, enemycount
        shootCounter = 0;
        enemycount = 0;


        try{
            Scanner config = new Scanner(getClass().getClassLoader().getResourceAsStream("resources/configs/level" + level + ".cfg.txt"));
            
            String[] spos = config.nextLine().split(",");
            int[] startpos = new int[]{Integer.parseInt(spos[0]), Integer.parseInt(spos[1])};
            offset[XVAL] = -(startpos[XVAL] - 750);  //creating the offsets
            offset[YVAL] = -(startpos[YVAL] - 500);
            int startwep = Integer.parseInt(config.nextLine());
            config.nextLine();  //get the configurations for the levels
            String mapfolder = config.nextLine();
            config.nextLine();
            spawnFans = Boolean.parseBoolean(config.nextLine());

            if(spawnFans){
                fanspawnRate = Integer.parseInt(config.nextLine());
                for(String coords : config.nextLine().split(" ")){
                    String[] xy = coords.split(",");
                    fanspawnLocs.add(new int[]{Integer.parseInt(xy[0]), Integer.parseInt(xy[1])});  //place for spawning
                }
            }


            spawnPap = Boolean.parseBoolean(config.nextLine());
            config.nextLine();
            if(spawnPap){
                papspawnRate = Integer.parseInt(config.nextLine());
                for(String coords : config.nextLine().split(" ")){
                    String[] xy = coords.split(",");
                    papspawnLocs.add(new int[]{Integer.parseInt(xy[0]), Integer.parseInt(xy[1]), 0});  //locations for paparazzi spawning
                }
            }
            spawnPol = Boolean.parseBoolean(config.nextLine());
            if(spawnPol){
                polspawnRate = Integer.parseInt(config.nextLine());
                for(String coords : config.nextLine().split(" ")){
                    String[] xy = coords.split(",");
                    polspawnLocs.add(new int[]{Integer.parseInt(xy[0]), Integer.parseInt(xy[1]), 0});  //locations for police spawning
                }
            }

            /*InputStream soundtrack = new FileInputStream("resources/sounds/playmusic.wav");
            kanyebeats = new AudioStream(soundtrack);
            AudioPlayer.player.start(tronstream); //start music*/

            //loading all the images
            minimap = ImageIO.read(getClass().getResourceAsStream("resources/images/" + mapfolder + "/minimap.jpg"));

       
            map = ImageIO.read(getClass().getResourceAsStream("resources/images/" + mapfolder + "/map.jpg"));

            mapmask = ImageIO.read(getClass().getResourceAsStream("resources/images/" + mapfolder + "/mask.jpg"));

         
            enemyPics = new BufferedImage[]{
                    ImageIO.read(getClass().getResourceAsStream("resources/images/enemy/backpackguy.png")),
                    ImageIO.read(getClass().getResourceAsStream("resources/images/enemy/brownguy.png")),
                    ImageIO.read(getClass().getResourceAsStream("resources/images/enemy/greengirl.png")),
                    ImageIO.read(getClass().getResourceAsStream("resources/images/enemy/paparazziwalking.png")),
                    ImageIO.read(getClass().getResourceAsStream("resources/images/enemy/paparazzitakingpic.png"))};
          

            explosiongif = ImageIO.read(getClass().getResourceAsStream("resources/images/animations/explosion.gif"));
            healthgif = ImageIO.read(getClass().getResourceAsStream("resources/images/animations/healthpack.gif"));
            yeezygif = ImageIO.read(getClass().getResourceAsStream("resources/images/animations/yeezy.gif"));
            vestgif = ImageIO.read(getClass().getResourceAsStream("resources/images/animations/vest.gif"));
            allPointers = new BufferedImage[]{ImageIO.read(getClass().getResourceAsStream("resources/images/pointers/redpointer.png")),
                                              ImageIO.read(getClass().getResourceAsStream("resources/images/pointers/greenpointer.png")),
                                              ImageIO.read(getClass().getResourceAsStream("resources/images/pointers/yellowpointer.png")),
                                              ImageIO.read(getClass().getResourceAsStream("resources/images/pointers/pinkpointer.png")),
                                              ImageIO.read(getClass().getResourceAsStream("resources/images/pointers/bluepointer.png"))};
            //loading the weapons
            allweps = new Weapon[]{new AssaultRifle(ImageIO.read(getClass().getResourceAsStream("/resources/images/animations/assaultrifle.gif")), ImageIO.read(getClass().getResourceAsStream("resources/images/player/assaultrifle.png")), 50),
                                   new Shotgun(ImageIO.read(getClass().getResourceAsStream("/resources/images/animations/shotgun.gif")), ImageIO.read(getClass().getResourceAsStream("resources/images/player/shotgun.png")), 85),
                                   new Flamethrower(ImageIO.read(getClass().getResourceAsStream("/resources/images/animations/flamethrower.gif")), ImageIO.read(getClass().getResourceAsStream("resources/images/player/flamethrower.png")), 1, 20),
                                   new RocketLauncher(ImageIO.read(getClass().getResourceAsStream("/resources/images/animations/rocketlauncher.gif")), ImageIO.read(getClass().getResourceAsStream("resources/images/player/rocketlauncher.png")), 350),
                                   new Arrow(ImageIO.read(getClass().getResourceAsStream("/resources/images/animations/arrow.gif")), ImageIO.read(getClass().getResourceAsStream("resources/images/player/arrow.png"))),
                                   new Pistol(ImageIO.read(getClass().getResourceAsStream("/resources/images/animations/pistol.gif")), ImageIO.read(getClass().getResourceAsStream("resources/images/player/pistol.png")), 150),
                                   new Chainsaw(ImageIO.read(getClass().getResourceAsStream("/resources/images/animations/chainsaw.gif")), ImageIO.read(getClass().getResourceAsStream("resources/images/player/chainsaw.png")))};
            
            trippic = ImageIO.read(getClass().getResourceAsStream("resources/images/tripmine.png"));
            papvanPic = ImageIO.read(getClass().getResourceAsStream("resources/images/van.png"));
            polvanPic= ImageIO.read(getClass().getResourceAsStream("resources/images/policecar.png"));
            
            //creating a new player
            player = new Kanye(startpos[0], startpos[1], allweps[0]);

            //loading three different font
            hudf = Font.createFont(Font.TRUETYPE_FONT, new File("resources/fonts//GrandQuatre.ttf")).deriveFont(150f);
            smallerf = Font.createFont(Font.TRUETYPE_FONT, new File("resources/fonts//GrandQuatre.ttf")).deriveFont(80f);
            smallestf = Font.createFont(Font.TRUETYPE_FONT, new File("resources/fonts//GrandQuatre.ttf")).deriveFont(5f);

            System.out.println("SUCCESS");
            config.close();

        }catch(IOException e){ System.out.println(e); } catch(FontFormatException f){ System.out.println("FONT ERROR"); }
	}

	public void addNotify() {
        super.addNotify();
        requestFocus();
        mainFrame.start();
    }

    public void keyTyped(KeyEvent e) {}

    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
        if(e.getKeyChar() == 'e') {
            tripmines.add(new TripMine(player.getX(), player.getY(), trippic));  //when e is pressed , a new tripmine is generate
        }
    }

    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
    }

	public void mouseClicked(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}

	public void mouseReleased(MouseEvent e){
        //when the mouse is released, the arrow is shot if it is held down 
        if (player.getWep().toString().equals("arrow") && ((Arrow)player.getWep()).getdrawTime() > 0){
            ((Arrow)player.getWep()).release();
        }

        player.setShooting(false);  //don't shoot when it is released

	}
	public void mousePressed(MouseEvent e){
        player.setShooting(true);

	}


    public void mouseDragged(MouseEvent e){
		mx = e.getX();
		my = e.getY();
        //angle changes
        player.changeAng(Math.atan2(my - (player.getY() + offset[YVAL]), mx - (player.getX() + offset[XVAL])));
	}

	public void mouseMoved(MouseEvent e) {
		mx = e.getX();
		my = e.getY();
        //angle changes
        player.changeAng(Math.atan2(my - (player.getY() + offset[YVAL]), mx - (player.getX() + offset[XVAL])));
	}

    //requires two lists; one of players coords on screen and player coords on map??
    public void move() {
        //moving with the screen
        if (keys[KeyEvent.VK_D] && !isColliding(player.getX() + 45, player.getY())) { //strafe right
            System.out.println("right");
            if (player.getX() + offset[0] <= 1350) {
                player.move(1, 0);
            } else if (offset[XVAL] >= -map.getWidth() + 1500) {
                player.move(1, 0);
                offset[XVAL] -= player.getSpeed();   //offsett value dedcreaess with the speed when moving left 
     
            }
        }
        if (keys[KeyEvent.VK_A] && !isColliding(player.getX() - 45, player.getY())) { //strafe left
            System.out.println("left");
            if (player.getX() + offset[XVAL] >= 150) {
                player.move(-1, 0);
            } else if (offset[XVAL] < 0) {
                player.move(-1, 0);
                offset[XVAL] += player.getSpeed();  //increaes with speed when moving right
       
            }
        }
        if (keys[KeyEvent.VK_W] && !isColliding(player.getX(), player.getY() - 45)) { //move UP
            System.out.println("up");
            if (player.getY() + offset[YVAL] >= 150) {
                player.move(0, -1);  
            } else if (offset[YVAL] < 0) {
                player.move(0, -1);
                offset[YVAL] += player.getSpeed();  //exact same thing but with y coordinates
         
            }
        }
        if (keys[KeyEvent.VK_S] && !isColliding(player.getX(), player.getY() + 45)) { //move DOWN
            System.out.println("down");
            if (player.getY() + offset[YVAL] <= 850) {
                player.move(0, 1);
            }
            else if (offset[YVAL] >= -map.getHeight() + 1000) {
                player.move(0, 1);
                offset[YVAL] -= player.getSpeed(); //same thing with y coordinates

            }
        }
        updateGame();  //call update game

    }
        
    public void updateGame(){
        player.updatePlayer();;
        if(player.getHP() <= 0){
            System.exit(0);
        }
        //main counter increases with shootcounter to a maximum amount
        mainCounter++;
        if (mainCounter++ >= Integer.MAX_VALUE){
            mainCounter = 0;

        }
        shootCounter++;
        if (shootCounter++>= Integer.MAX_VALUE){
            shootCounter = 0;
        }
        //every 2000 milliseconds and if the weaponlist has a size of 2 generate a newweapon
        if(mainCounter % 2000==0 && weaponlist.size() <= 2){
            int choice = rn.nextInt(allweps.length);
            int[] randpos = getValidPoints();  //random poinnts on screen
            weaponlist.add(new int[]{randpos[XVAL], randpos[YVAL], choice});
        }
        //every 1000 milliseconds and only one health kit is there
        if(mainCounter% 1000 == 0 && healthkit == null){
            int[] randpos = getValidPoints();
            healthkit = new Health(randpos[0], randpos[1], healthgif, allPointers[0]);
        }
        //generate a new powerup ever 200 milliseconds
        if (mainCounter %200==0 && powerList.size() <=2){
            int choice = rn.nextInt(2);
            int[] randpos = getValidPoints();
            if (choice == 0){
                powerList.add(new Powerup("yeezys", randpos[0], randpos[1], 4000, yeezygif, allPointers[1]));
            }
            else{
                powerList.add(new Powerup("vest", randpos[0], randpos[1], 2500, vestgif, allPointers[4]));
            }

        }
        //makes sure player is shooting and is not tased
		if((player.isShooting() && !player.getTased() && shootCounter >= player.getWep().getFirerate()) || (player.getWep().toString().equals("arrow"))){

            if (player.getWep().toString().equals("arrow") && !((Arrow)player.getWep()).isReleased()){  //makes s ure the arrow is not released
                ((Arrow)player.getWep()).drawBow();  //draw the counter for the arrow
                if(((Arrow)player.getWep()).getdrawTime() >= 250){   //makes sure the arrow is realeased at the 250 milliseconds
                    ((Arrow)player.getWep()).release();
                }
            }
            else{
                //otherwise draw the gun at a certain offset
                double tmpx = (14/Math.cos(Math.toRadians(90) - player.getAng()) + ( 24 - 14*Math.tan(Math.toRadians(90)- player.getAng())))* Math.cos(player.getAng());
                double tmpy = (24 - 14*Math.tan(Math.toRadians(90)- player.getAng()))* Math.sin(player.getAng());
                //use math to figure out the position of the gun relative to the angle
                //if (tmpang > 90 && tmpang< 270){
                 //   bullets = player.getWep().shoot(player.getX() + tmpx, player.getY()+tmpy, player.getAng(), bullets);
              //  }
             //   else{
                // shoot the bullets
                bullets = player.getWep().shoot(player.getX() + tmpx, player.getY()-tmpy, player.getAng(), bullets);
                //}


            }
            shootCounter = 0;
		}
        //adding new fans as long as there is a certain amount
        if(spawnFans && mainCounter % fanspawnRate == 0 && fans.size() <= 50){
            int randspawn = rn.nextInt(fanspawnLocs.size());
            fans.add(new Fan(fanspawnLocs.get(randspawn)[0], fanspawnLocs.get(randspawn)[1], 50, 50, enemyPics[rn.nextInt(3)]));
        }
    
        //adding new papparazzi as long as there is a certiain amount
        if(spawnPap && mainCounter % papspawnRate == 0 && paparazzi.size() < papspawnLocs.size()){
            int randspawn = rn.nextInt(papspawnLocs.size());
            vanList.add(new Van(randspawn, papspawnLocs.get(randspawn)[0], papspawnLocs.get(randspawn)[1], papvanPic));
            paparazzi.add(new Paparazzi(papspawnLocs.get(randspawn)[0], papspawnLocs.get(randspawn)[1], 50, vanList.get(0)));
            papspawnLocs.get(randspawn)[2] = 1;
        }
        //adding new police as long as there is a certain amount and the rate works
        if(spawnPol && mainCounter % polspawnRate == 0 && policelist.size() < polspawnLocs.size()){
            int randspawn = rn.nextInt(polspawnLocs.size());
            vanList.add(new Van(randspawn, polspawnLocs.get(randspawn)[0], polspawnLocs.get(randspawn)[1], polvanPic));
            policelist.add(new Police(polspawnLocs.get(randspawn)[0], polspawnLocs.get(randspawn)[1], 50, vanList.get(0)));
            polspawnLocs.get(randspawn)[2] = 1;
        }
        //endregion


        //region COLLISION DETECTION BETWEEN OBJECTS'
        //picking up health
        if(healthkit != null){
            if(player.collide(healthkit.getX(), healthkit.getY(), 30)){
                player.pickup(healthkit);
                healthkit = null;

            }
        }
        //picking up powerups
        for(Iterator<Powerup> powerupIterator = powerList.iterator(); powerupIterator.hasNext();){
            Powerup p = powerupIterator.next();
            if(player.collide(p.getX(), p.getY(), 30)){
                player.pickup(p);
                powerupIterator.remove();  //remove when done

            }
        }
        //picking up weapons
        for (Iterator<int[]> weaponIter = weaponlist.iterator(); weaponIter.hasNext();) {
            int[] w = weaponIter.next();
            if (player.collide(w[0], w[1], 30)){  // the x and y coordinates are the w[0] and w[1]
                player.pickup(allweps[w[2]]);  //the type is the third spot of the  array and is coorrelated to the allweps array
                weaponIter.remove();  //remove when done
            }
        }
        //Explosions do splash damage based on how far away the enemy is to them, only explodes once then is gone\
        //exploding the explosions
        for (Iterator<Explosion> exploiter = explosions.iterator(); exploiter.hasNext(); ) {
            Explosion exp = exploiter.next();
            for(Enemy e : fans){
                e.changeHP((int)Math.round(-exp.boom(e.getX(), e.getY())));
            }
            exploiter.remove();
        }
        //exploding if it collides with the three enemiews
        for (Iterator<TripMine> mineiter = tripmines.iterator(); mineiter.hasNext(); ) {
            TripMine t = mineiter.next();
            for(Fan e : fans){
                if(e.collide(t.getX(), t.getY(), 50)){
                    explosions.add(t.blowUP());
                    mineiter.remove();
                }
            }
            for(Police e : policelist){
                if(e.collide(t.getX(), t.getY(), 50)){
                    explosions.add(t.blowUP());
                    mineiter.remove();
                }
            }
            for(Paparazzi e : paparazzi){
                if(e.collide(t.getX(), t.getY(), 50)){
                    explosions.add(t.blowUP());
                    mineiter.remove();
                }
            }

        }

        for (Iterator<Bullet> buliter = bullets.iterator(); buliter.hasNext(); ) {
			Bullet b = buliter.next();
            boolean remove = false;   //removing the bulltsare initially ffalse
            int impact = 0;
            double impactangle;
            Color impactCol;

            b.move();  //move the bullet
            if (b.getX() > map.getWidth() || b.getX() < 0 || b.getY() > map.getWidth() || b.getY() < 0 || b.getSpeed() <=  0.5 || player.getWep().toString().equals("chainsaw") || isColliding(b.getX(), b.getY())) {
                remove = true;  //all the different types of restrictions for removingn the bullets
            }
            //remove the bullet if collision occurs
            if(isColliding(b.getX(), b.getY())){
                remove = true;
                impact = 1;
            }


            //when player gets hit by a taser, they cant move or shoot
            if (player.collide(b.getX(), b.getY(), 35) && b.getType() == 5) {
                player.setTased(true);
                impact = 2;
            }
            //when the police gets hit by a bullet
            for(Police e : policelist){//.addAll(paparazzi)){
                if (b.getType()!=5 && e.collide(b.getX(), b.getY(), 35)){
                    e.changeHP(-b.getDamage());
                    remove = true;
                    impact = 2;
                    if (player.getWep().toString().equals("pistol")) {
                        e.changeHP(-e.getHP()); //one hit kill
                    }
                    else if (player.getWep().toString().equals("arrow")) {
                        e.changeHP(-e.getHP()); //one hit kill
                        remove = false; //collateral damage
                    }
                    else if (player.getWep().toString().equals("flamethrower")) {
                        e.setonFire(true); //burn to death
                        remove = false;
                    }
                }
            }
            //same as fans
            for(Fan e : fans){//.addAll(paparazzi)){
                if(e.collide(b.getX(), b.getY(), 35)){
                    e.changeHP(-b.getDamage());
                    remove = true;
                    impact = 2;
                    if (player.getWep().toString().equals("pistol")){
                        e.changeHP(-e.getHP());
                    }
                    else if (player.getWep().toString().equals("arrow")){
                        e.changeHP(-e.getHP());
                        remove = false;
                    }
                    else if(player.getWep().toString().equals("flamethrower")){
                        e.setonFire(true);
                        remove = false;
                    }
                }
            }
            //same as paparazzi
            for(Paparazzi e : paparazzi){//.addAll(paparazzi)){
                if(e.collide(b.getX(), b.getY(), 35)){
 
                    e.changeHP(-b.getDamage());

                    remove = true;
                    impact = 2;

                    if(player.getWep().toString().equals("pistol")){
                        e.changeHP(-e.getHP());
                    }
                    else if (player.getWep().toString().equals("arrow")){
                        e.changeHP(-e.getHP());
                        remove = false;
                    }
                    else if(player.getWep().toString().equals("flamethrower")){
                        e.setonFire(true);
                        remove = false;
                    }
                }
            }

            if (impact != 0 && isValidPoint(b.getX(), b.getY())) { //bullet has collided with something
                if (b.getAng() < 0) {
                    impactangle = Math.PI + b.getAng();   //angle of impact and is above the rangge
                } else {
                    impactangle = b.getAng() - Math.PI;
                }
                if (impact == 1) { //collided with environment
                    impactCol = new Color(map.getRGB((int)Math.round(b.getX()), (int)Math.round(b.getY())));   //colour is dependedt on where it hits
                } else {
                    impactCol = new Color(255, 0, 0, rn.nextInt(181) + 75);
                }
                for (int i = 0; i < rn.nextInt(2) + 5; i++) {
                    impactlist.add(new double[]{b.getX(), b.getY(), impactangle += rn.nextDouble() * (Math.PI / 3) - (Math.PI / 6), 5, impactCol.getRed(), impactCol.getGreen(), impactCol.getBlue(), impactCol.getAlpha()});
                }
            }
            if(remove){      //creattes a new explosion if the player's weapon is a rocketlauncher
                if(player.getWep().toString().equals("rocket launcher")){
                    explosions.add(new Explosion(b.getX(), b.getY(), 80, 250));
                    allAnimations.add(new Animation("explosion", b.getX(), b.getY(), 80, explosiongif));
                }
                buliter.remove(); //remove the bullet
            }
		}
        //run through the fans
        for(Iterator<Fan> faniter = fans.iterator();  faniter.hasNext();) {
            Fan tmpf  = faniter.next();
            double impactangle;
            tmpf.move(mapmask, player, fans, paparazzi, policelist);   //move the fans
            if(tmpf.attack(player)){
                if(tmpf.getAng() < 0){
                    impactangle = Math.PI + tmpf.getAng();  //calculate the impact angle
                }else{
                    impactangle = tmpf.getAng() - Math.PI;
                }
                for(int i = 0; i < rn.nextInt(2) + 5; i++){
                    impactlist.add(new double[]{player.getX(), player.getY(), impactangle += rn.nextDouble() * (Math.PI / 3) - (Math.PI / 6), 5, 255, 0, 0, rn.nextInt(181) + 75});
                }
            }
            if(tmpf.isonFire() && mainCounter % 100 == 0){ //slowly burn to death
                tmpf.changeHP(-3);
            }
            //remove the fans if it's health is less than 0
            if(tmpf.getHP() <= 0){
                faniter.remove();
                enemycount +=1;
            }
        }
        //the same thin for paparazzi
        for(Iterator<Paparazzi> pappIter = paparazzi.iterator();  pappIter.hasNext();){
            Paparazzi tmpp = pappIter.next();

            tmpp.move(mapmask, player, paparazzi, fans, policelist);
            
            if(tmpp.isonFire() && mainCounter % 150 == 0){  //burn to death
                tmpp.changeHP(-3);
            }
            
            if(tmpp.checkRemove()){
                vanList.remove(tmpp.getHome()); //try and make this slowly fade away
                pappIter.remove();
                enemycount+=1;  //the tally of enemies diying increases
                for (int i  = 0; i < 10; i++) {
                    int[] tmppos = getValidPoints();
                    fans.add(new Fan(tmppos[XVAL], tmppos[YVAL], 50, 50, enemyPics[rn.nextInt(3)]));  //if a picture is taken generate 10 new fans
                }
                papspawnLocs.get(tmpp.getHome().getSpot())[2] = 0; //frees up the spot again
            }


        }
        //same thing for police
        for(Iterator<Police> polIter = policelist.iterator();  polIter.hasNext();){
            Police tmpp = polIter.next();

            tmpp.move(player, paparazzi, fans, policelist, bullets);

            if(tmpp.isonFire() && mainCounter % 150 == 0){
                tmpp.changeHP(-3);  //burn
            }

            if(tmpp.checkRemove()){
                vanList.remove(tmpp.getHome()); //try and make this slowly fade away
                polIter.remove();
                enemycount+=1;  //increase enemy total
                polspawnLocs.get(tmpp.getHome().getSpot())[2] = 0;

            }



        }

        //endregion
	}

    //for a point to be valid, it has to be off the current screen (not spawning enemies right next to the player, unfair), on the map and not colliding with any walls
    public int[] getValidPoints(){
        int randx, randy;
        do{
            randx = rn.nextInt(map.getWidth());          //create a new spot on the map
            randy = rn.nextInt(map.getHeight());
        }while(!isOffscreen(randx, randy) || isColliding(randx, randy)); //has to be on map, not colliding, and off the screen

        return new int[]{randx, randy};

    }
    // checks if the point is offscreen or not
    public boolean isOffscreen(double ox, double oy){
        if(player.getX() - ox > player.getX() + offset[XVAL] || player.getY() - oy > player.getY() + offset[YVAL] || 1500 - (player.getX() + offset[XVAL]) < ox - player.getX() || 1000 - (player.getY() + offset[YVAL]) < oy - player.getY()){
            return true;
        }
        return false;
    }
    //check mask collision
    public boolean isColliding(double ox, double oy){ //checks mask to see if its colliding with points
        if(isValidPoint(ox, oy)){
            //System.out.println(mapmask.getRGB((int)Math.round(ox), (int)Math.round(oy)));
            return new Color(mapmask.getRGB((int)Math.round(ox), (int)Math.round(oy))).equals(Color.black);
        }
        return true;
    }
    
    public boolean isValidPoint(double ox, double oy){ //checks if point is on actual map
        return (int)Math.round(ox) >= 0 && (int)Math.round(ox) < map.getWidth() && (int)Math.round(oy) >= 0 && (int)Math.round(oy) < map.getHeight();
    }

    //drawing the hud
    public void drawHUD(Graphics2D g2){
        g2.drawImage(minimap, 20, 20, this);
        g2.setStroke(new BasicStroke(1));
        g2.setColor(Color.blue);
        //this part is the mini map where fans, powerups, weapons, paparazzi, police are shown 
        g2.fillOval((int)Math.round(player.getX() / (map.getWidth() / 200)) + 15, (int) Math.round(player.getY() / (map.getHeight() / 200)) + 15, 10, 10);

        g2.setColor(new Color(255, 16, 0));
        for(Fan f : fans){
            g2.fillOval((int)Math.round(f.getX() / (map.getWidth() / 200)) + 15, (int) Math.round(f.getY() / (map.getHeight() / 200)) + 15, 7, 7);
        }

        g2.setColor(Color.blue);
        for(Police p : policelist) {
            g2.fillOval((int) Math.round(p.getX() / (map.getWidth() / 200)) + 15, (int) Math.round(p.getY() / (map.getHeight() / 200)) + 15, 7, 7);
        }
        g2.setColor(Color.cyan);
        for(int[] p: weaponlist){
            g2.fillOval((int) Math.round(p[0] / (map.getWidth() / 200)) + 15, (int) Math.round(p[1] / (map.getHeight() / 200)) + 15,7,7);
        }

        g2.setColor(new Color(255, 244, 14));
        for(Paparazzi p : paparazzi){
            g2.fillOval((int)Math.round(p.getX() / (map.getWidth() / 200)) + 15, (int) Math.round(p.getY() / (map.getHeight() / 200)) + 15, 7, 7);
        }
        if(healthkit != null){
            g2.setColor(Color.pink);
            g2.fillOval((int)Math.round(healthkit.getX() / (map.getWidth() / 200)) + 15, (int) Math.round(healthkit.getY() / (map.getHeight() / 200)) + 15, 7, 7);
        }

        g2.setColor(new Color(0, 255, 35));
        for(Powerup yv : powerList){
            g2.fillOval((int)Math.round(yv.getX() / (map.getWidth() / 200)) + 15, (int) Math.round(yv.getY() / (map.getHeight() / 200)) + 15, 7, 7);

        }
        //you must go to the helicopter iff the enemy count is 500
        if(enemycount >=500){
            g2.setColor(Color.white);
            g2.drawOval(Math.round(6000 / (map.getWidth() / 200) + 15),  Math.round(100 / (map.getHeight() / 200)) + 15, 10, 10);
            double newx, newy;
            double newang = Math.atan2(5260 - player.getY(), 950- player.getX());
            if (Line2D.linesIntersect(player.getX(),player.getY(), 5260, 950, 25 - offset[0], 25-offset[1], 1475-offset[0], 25 - offset[1]) || Line2D.linesIntersect(player.getX(),player.getY(), 5260, 950, 25-offset[0], 975-offset[1], 1475-offset[0], 975 - offset[1] )){
                //however they can intersect twice from the bottom and the top so
                //check if the player's y coordinate is less than the one of the weaponns
                if (player.getY() > 950){
                    newy = -player.getY() -offset[1] + 50;  //the y coordinate of the pointer relative to the player is known
                    newx = newy*1/Math.tan(newang); //from this since the y value is known and the angle is known then the x value is the y value * cotangent(ang)
                }
                //if the player's y coordinate less than the one of the paparazzi's
                else{
                    //System.out.println("drawing  bottom");

                    newy = -offset[1] + 850 - player.getY();  //the y coordinate is  known so using trig, the x  coordinate can be found
                    newx = newy*1/Math.tan(newang);
                }
            }
            else{ //creates lines with x = 0 and x = 1500
                if(player.getX() < 5260){  //checks if the enemy is on the left
                    newx = 1400 - offset[0] -player.getX(); //the x coordinate can be found using subtraction making it 100 coordinates to the right of x= 0
                    newy = newx* Math.tan(newang);//use this to find the y coordinate similarly to the one above
                }
                else{

                    newx= -offset[0] + 100 - player.getX();  //do the same for tthe other side
                    newy= newx*Math.tan(newang);
                }

            }

           // AffineTransform oldAT = g2.getTransform(); //save default transformations
           // g2.translate((int)Math.round(player.getX() + newx + offset[0]), (int)Math.round(player.getY() + newy + offset[1])); //move graphics2d object to center of image
           // g2.rotate(Math.atan2(950 - player.getY(), 5260 - player.getX())); //rotate around the center of image
           g2.drawImage(allPointers[2], -50, -50, this); //codords are top left of image, gun sticks out 42 pixels
           // g2.setTransform(oldAT); //reset
            //g2.setTransform(oldAt);
        }




        g2.drawRect(Math.round(-offset[0] / (map.getWidth() / 200)) + 20, Math.round(-offset[1] / (map.getHeight() / 200)) + 20, 1500/(map.getWidth() / 200), 1000/ (map.getHeight() / 200));
        g2.setColor(Color.white);
        //g2.setColor(new Color(202, 2, 5)); //white or red?
        g2.setFont(hudf);
        g2.drawString("" + player.getHP(), 30, 930);
        g2.setColor(new Color(255,255,255, 100)); 
        g2.fillRect(200, 833, 50,50); // speedboost rect
        g2.fillRect(260, 833, 50,50);//hpBoost rect
        
        if(player.getspeedPower() != null) {         //draw the immage of the yeezy if it is picked up
            if (!player.getspeedPower().ranOut()) {
                g2.drawImage(yeezygif, 200, 833, this);
                if (player.getspeedPower().getDur() < (player.getspeedPower().getOGDur() / 10)) {
                    if (player.getspeedPower().getDur() % 50 % 2 == 0) {
                        g2.drawImage(yeezygif, 260, 833, this);
                    }
                } else {
                    g2.drawImage(yeezygif, 260, 833, this);
                } 
            }
        }
        //drawing the hp power if it is picked up
        if(player.gethpPower() != null) {
            if (!player.gethpPower().ranOut()) {
                if (player.gethpPower().getDur() < (player.gethpPower().getOGDur() / 10)) {
                    if (player.gethpPower().getDur() % 50 % 2 == 0) {
                        g2.drawImage(vestgif, 260, 833, this);
                    }
                }else {
                    g2.drawImage(vestgif, 260, 833, this);
                }
            }
        }
        g2.setFont(smallerf);
        g2.setColor(Color.white);
        g2.drawString("" + player.getWep().toString(),  25, 810);
        g2.setFont(smallestf);
        g2.drawString(" " +  enemycount, 345, 890);

    }


    public void paintComponent(Graphics g){
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(map, offset[XVAL], offset[YVAL], this);  //draw the map
        if(healthkit != null){
            if (!isOffscreen(healthkit.getX(), healthkit.getY())) {
                healthkit.draw(g2, this, offset, (int)Math.round(player.getX()), (int)Math.round(player.getY()), false);
                //draws on screen
            }
            else{
                healthkit.draw(g2, this, offset, (int)Math.round(player.getX()), (int)Math.round(player.getY()), true);
                //draws the pointers
            }
        }

        for(Bullet b : bullets){
            if(!isOffscreen(b.getX(), b.getY())){  //draws if on screen
                b.draw(g2, this, offset);
            }

		}
        for (int[] weapon : weaponlist){
            Weapon w = allweps[weapon[2]];
            if(!isOffscreen(weapon[0], weapon[1])){
                
            }
            else{
                //pointers towards the weapons
                double newx, newy;
                double newang = Math.atan2(weapon[1] - player.getY(), weapon[0] - player.getX());
                if (Line2D.linesIntersect(player.getX(),player.getY(), weapon[0], weapon[1], 25 - offset[0], 25-offset[1], 1475-offset[0], 25 - offset[1]) || Line2D.linesIntersect(player.getX(),player.getY(), weapon[0], weapon[1], 25-offset[0], 975-offset[1], 1475-offset[0], 975 - offset[1] )){
                    //however they can intersect twice from the bottom and the top so
                    //check if the player's y coordinate is less than the one of the weaponns
                    if (player.getY() > weapon[1]){
                        newy = -player.getY() -offset[1] + 50;  //the y coordinate of the pointer relative to the player is known
                        newx = newy*1/Math.tan(newang); //from this since the y value is known and the angle is known then the x value is the y value * cotangent(ang)
                    }
                    //if the player's y coordinate less than the one of the paparazzi's
                    else{
                        //System.out.println("drawing  bottom");

                        newy = -offset[1] + 850 - player.getY();  //the y coordinate is  known so using trig, the x  coordinate can be found
                        newx = newy*1/Math.tan(newang);
                        }
            }
                else{ //creates lines with x = 0 and x = 1500
                    if(player.getX() < weapon[0]){  //checks if the enemy is on the left
                        newx = 1400 - offset[0] -player.getX(); //the x coordinate can be found using subtraction making it 100 coordinates to the right of x= 0
                        newy = newx* Math.tan(newang);//use this to find the y coordinate similarly to the one above
                        }
                else{

                        newx= -offset[0] + 100 - player.getX();  //do the same for tthe other side
                        newy= newx*Math.tan(newang);
                    }

                }

                AffineTransform oldAT = g2.getTransform(); //save default transformations
                g2.translate((int)Math.round(player.getX() + newx + offset[0]), (int)Math.round(player.getY() + newy + offset[1])); //move graphics2d object to center of image
                g2.rotate(Math.atan2(weapon[1] - player.getY(), weapon[0] - player.getX())); //rotate around the center of image
                g2.drawImage(allPointers[2], -50, -50, this); //codords are top left of image, gun sticks out 42 pixels
                g2.setTransform(oldAT); //reset
                //g2.setTransform(oldAt);
            }
            g2.setColor(Color.cyan);
            g2.drawImage(w.getSprite(),weapon[0]+ offset[0], weapon[1]+offset[1], this);
          //  g2.drawString(""+ w.toString(), weapon[0]+ offset[0], weapon[1]+offset[1]);

        }
        //draws the police if not onscreen
        for (Police p :policelist){
            if(isOffscreen(p.getX(), p.getY())){ //arrow will point to enemy if offscreen
                p.draw(g2, offset, true, enemyPics, player.getX(), player.getY());
            }
            else{
                p.draw(g2, offset, false, enemyPics, player.getX(), player.getY());
            }
        }
        //draws fans
        for(Fan e: fans){
            if(!isOffscreen(e.getX(), e.getY())){
                e.draw(g2, this, offset);
            }
        }
        //draws tripminnes
        for (TripMine t : tripmines){
            if(!isOffscreen(t.getX(), t.getY())){
                t.draw(g2, this, offset);
            }
        }
        //draws impacts
        for(Iterator<double[]> impactIter = impactlist.iterator(); impactIter.hasNext();){
            double[] impact = impactIter.next();

            g.setColor(new Color((int)impact[4], (int)impact[5], (int)impact[6], (int) impact[7]));
            g.fillRect((int)(impact[0] + offset[0]),(int)(impact[1] + offset[1]), 10, 10);

            impact[0] += impact[3] * Math.cos(impact[2]); //move in said direction
            impact[1] += impact[3] * Math.sin(impact[2]);
            impact[3] *= Math.random() / 12.5 + 0.90; //random deceleration from 0.9 to 0.98
            if(impact[3] <= 1){
                impactIter.remove();
            }

        }
        //draws paparazzi
        for (Paparazzi p: paparazzi){
            if(!isOffscreen(p.getX(), p.getY())){ //arrow will point to enemy if offscreen
                p.draw(g2, offset, enemyPics, this);
            }
        }
        //draws vans
        for(Van v : vanList){
            if(!isOffscreen(v.getX(), v.getY())){
                v.draw(g2, this, offset);
            }
        }
        //draws powerups
        for (Powerup p : powerList){
            if(!isOffscreen(p.getX(), p.getY())){
                p.draw(g2, this, offset, (int)Math.round(player.getX()), (int)Math.round(player.getY()), false);
            }
            else{
                p.draw(g2, this, offset, (int)Math.round(player.getX()), (int)Math.round(player.getY()), true);
            }
        }
        //draws animations

        for(Iterator<Animation> animIter = allAnimations.iterator();  animIter.hasNext();){
            Animation a = animIter.next();
            if(!isOffscreen(a.getX(), a.getY())){
                g2.drawImage(a.update(), (int)Math.round(a.getX() + offset[0] - a.getImage().getWidth(this) / 2), (int)Math.round(a.getY() + offset[1] - a.getImage().getHeight(this) / 2), this);
            }
            if(a.isFinished()){
                animIter.remove();
            }
        }
        //draws arrows
        //if (player.getWep().toString().equals("arrow") ){
           // ((Arrow)player.getWep()).draw(g2, player, offset);

        //}
        //draw player and hud
        player.draw(g2, this, offset);
        drawHUD(g2);
    }
}