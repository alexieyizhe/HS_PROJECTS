import java.util.*;
import java.io.*;

/*Pokemon Assignment
 *Alex Xie
 **/
public class PokemonArena{
	private final int NAME = 0;
	private final int HEALTH = 1; //amount of hp points remaining
	private final int TYPE = 2;
	private final int RESIST = 3;
	private final int WEAK = 4;
	private final int NUMATTACKS = 5; //number of attacks available
	private final int ATTACKS = 6; //list of attacks
	private final int ENERGY = 7; //energy level of current pokemon
	private final int STATE = 8; //special conditions, i.e. poisoned
	private final int ACTIVE = 9; //is the pokemon the one fighting or resting
	
	private static ArrayList<ArrayList<String>> highscores = new ArrayList<ArrayList<String>>(); //2d arraylist of highscores
	private static ArrayList<Pokemon> friendlies = new ArrayList<Pokemon>();
	private static ArrayList<Pokemon> enemies = new ArrayList<Pokemon>();
	private static ArrayList<Pokemon> graveyard = new ArrayList<Pokemon>();
	private static String playername;
	private static String validGame = "playing";
	private static Pokemon playerpoke; //negative 1 indicates no pokemon currently chosen
	private static Pokemon enemypoke;
	private static Scanner kb = new Scanner(System.in);
	private static Random rand = new Random();
	public static void main(String[] args)throws IOException{
		/*The main method loads the files needed to start the program, then executes the loop that opens the main menu of the game.*/
		
		Scanner hsfile = new Scanner(new BufferedReader(new FileReader("highscores.txt")));
		for(int i = 0; i < 5; i ++){
			highscores.add(new ArrayList<String>(Arrays.asList(hsfile.nextLine().split(" "))));
		}
		mainMenu();
		
	}
	
	////////////MAIN MENU////////////
	public static void mainMenu(){
		/*This is the main menu of the game. It will allow users to select a new game or continue a saved game. It will also allow a user to look at the leaderboard (high scores),
		 *look at the credits (not much there) and quit if they are done playing. After selecting each option, the program runs separate methods relevant to the user's selection.*/
		while(true){
			int startchoice = Integer.parseInt(input("POKEMON ARENA \n1. Start Game \n2. Continue Game \n3. High Scores \n4. Credits \n5. Quit \n\nOption #"));
			if(confirm(startchoice)){
				if(startchoice == 1){
					try{
						Scanner newfile = new Scanner(new BufferedReader(new FileReader("pokemon.txt")));
						while(newfile.hasNextLine()){
							enemies.add(new Pokemon(newfile.nextLine()));
						}
					}
					catch(IOException error){
						System.out.println("There was an error reading the file. The file may not exist in this folder. Try again. \nError Message: " + error.getMessage());
					}
					startGame();
				}
				else if(startchoice == 2){
					try{
						Scanner savefilef = new Scanner(new BufferedReader(new FileReader("savefilefriendly.txt"))); //reads in friendly pokemon from save file
						playername = savefilef.nextLine(); //first line of save file is player name
						playerpoke = new Pokemon(savefilef.nextLine()); //second line is active friendly pokemon
						while(savefilef.hasNextLine()){
							friendlies.add(new Pokemon(savefilef.nextLine())); //create new pokemon from lines in save file
						}
						Scanner savefilee = new Scanner(new BufferedReader(new FileReader("savefileenemy.txt"))); //read in enemy pokemon
						enemypoke = new Pokemon(savefilee.nextLine()); //first line is active enemy pokemon
						while(savefilee.hasNextLine()){
							friendlies.add(new Pokemon(savefilee.nextLine()));
						}
						continueGame();
					}
					catch(IOException error){
						System.out.println("There was an error reading the save file. The save file may not exist in this folder. Try again or start a new game. \nError Message: " + error.getMessage());
					}
				}
				else if(startchoice == 3){
					displayHS();
				}
				else if(startchoice == 4){
					System.out.println("everything was done by alex xie no plagiarism here mr mckenzie ;) \n(C) 2016 Alex Xie or something \n");
				}
				else if(startchoice == 5){
					break;
				}
				else{
					System.out.println("That isn't a valid choice!\n");
				}
			}
		}
	}
	
	////////////NEW GAME////////////
	public static void startGame(){
		System.out.println("Starting Game...");
		for(int i = 1; i < 11; i ++){
			System.out.printf("Loading...%d0\n", i); //fake loading bar
		}
		playername = input("\nWelcome to Pokemon Arena!\nYou have been tasked with defeating the undefeated reigning champion of the arena, Gym Leader Alex, by defeating \nall of his Pokémon that he has won from previous failed attempters.\nThe battle will be played out in a series of rounds until you win, or are defeated.\n\nWhat is your name, Trainer?\n");
		
		//PRINTS GRID SELECTION OF POKEMON
		gridDisplay(enemies, false);
		
		//USER CHOOSES FRIENDLIES AND ENEMIES
		while(friendlies.size() < 4){
			int picked = Integer.parseInt(input("I choose Pokémon #"));
			if(picked <= 28 && picked > 0){
				if(!friendlies.contains(enemies.get(picked - 1))){
					friendlies.add(enemies.get(picked - 1));
					System.out.printf("You have picked %s!\n", enemies.get(picked - 1).getName());
				}
				else{
					System.out.println("You already picked that Pokémon!");
				}
			}
			else{
				System.out.println("That is not a valid Pokémon choice!");
			}
		}
		for(Pokemon chosen : friendlies){
			enemies.remove(enemies.indexOf(chosen)); //get rid of friendly pokemon from enemy list
		}

		System.out.printf("\nYour team has been chosen, Trainer %s! \n\n", playername);
		gridDisplay(friendlies, false);
		System.out.println("Here's your team! \nChoose a Pokémon to start the battle for you!");
		playerpoke = pickPoke(friendlies); //starting active pokemon picked here
		enemypoke = enemies.get(rand.nextInt(enemies.size())); //AI randomly picks a starting enemy pokemon
		friendlies.remove(friendlies.indexOf(playerpoke));
		enemies.remove(enemies.indexOf(enemypoke));
		
		System.out.printf("I choose you, %s! \n\nThe Gym Leader has chosen %s! \n\n\nLet the battle begin! \n\n", playerpoke.getName(), enemypoke.getName());
		
		Battle();
	}
	
	////////////CONTINUE GAME////////////
	public static void continueGame(){
		System.out.printf("Welcome back, Trainer %s! \n", playername);
		System.out.println("Your save file is being loaded...");
		for(int i = 1; i < 11; i ++){
			System.out.printf("Loading...%d0\n", i);
		}
		Battle();
	}
	
	////////////DISPLAY HIGH SCORES////////////
	public static void displayHS(){
		System.out.printf("%5s %-16s %-17s %-13s %-3s \n", "Rank", "Name", "Defeated Pokemon", "Lost Pokemon", "Completed?"); //labels on table
		for(ArrayList<String> entry : highscores){
			System.out.printf("%5s %-16s %-17s %-13s %-3s \n", entry.get(0), entry.get(1), entry.get(2), entry.get(3), entry.get(4)); //entries are part of 2d arraylist containing high score info
		}
		System.out.println("\n");
	}
	
	////////////END OF GAME////////////
	public static void endGame(String outcome){
		if(outcome.equals("loss")){
			System.out.printf("Trainer %s, you've lost against Gym Leader Alex...\nThroughout the battle, you managed to defeat %d of his Pokemon.\n", playername, 23 - enemies.size());
		}
		else if(outcome.equals("win")){
			System.out.printf("Trainer %s, you've defeated all of Gym Leader Alex's Pokemon, winning the title of Gym Leader yourself! \nHowever, this win came at the cost of %d of your beloved Pokemon. Remember the efforts and sacrifices of your fallen Pokemon as you celebrate this win... \n", playername, 3 - friendlies.size());
		}
		int numadd = 0;
		ArrayList<String> listadd = new ArrayList<String>();
		boolean newhigh = false;
		for(ArrayList<String> indivscore : highscores){
			if(24 - enemies.size() >= Integer.parseInt(indivscore.get(2))){ //greater # of pokemon defeated is first
				numadd = Integer.parseInt(indivscore.get(0)) - 1; //cannot modify arraylist as it is iterating (causes ConcurrentModificationException)
				listadd =  new ArrayList<String>(Arrays.asList(indivscore.get(0), playername, Integer.toString(24 - enemies.size()), Integer.toString(3 - graveyard.size()), outcome.equals("win") ? "Yes" : "No")); //exactly as highscores.text original
				newhigh = true;
				break;
			}
		}
		if(newhigh){
			highscores.add(numadd, listadd); //add new high score
			highscores.remove(5); //remove lowest score (bumped off)
			try{
				PrintWriter hssave = new PrintWriter(new BufferedWriter (new FileWriter ("highscores.txt", false)));
				for(ArrayList<String> scores : highscores){
					hssave.println(String.format("%s %s %s %s %s", scores.get(0), scores.get(1), scores.get(2), scores.get(3), scores.get(4)));//exactly as highscores.text original
				}
				hssave.close();
			}	
			catch(IOException error){
				System.out.println("There was an error reading the save file. The save file may not exist in this folder. Try again or start a new game. \nError Message: " + error.getMessage());
			}
		}
		
		System.out.println("The game has ended! Thank you for playing Pokemon Arena!");
		System.exit(0);
	}	
	
	////////////BATTLE////////////
	public static void Battle(){
		boolean pstart = rand.nextInt(2) == 1 ? true : false; //player starts if random selects 1, same order throuh the entire game
		while(validGame.equals("playing")){
			Fight(pstart);
			if(enemypoke.getState().contains("Disabled")){ //disabled special lasts until end of one battle
				enemypoke.changeState("Disabled", true);
			}
			if(playerpoke.getState().contains("Disabled")){
				enemypoke.changeState("Disabled", true);
			}
			for(Pokemon friendlypoke : friendlies){
				if(friendlypoke.getState().contains("Disabled")){
					friendlypoke.changeState("Disabled", true);
				}
			}
		}
	}
	
	////////////ROUNDS////////////
	public static void Fight(boolean starter){
		for(int i = 0; i < 2; i ++){
			if(starter){
				roundChoice(false);
				starter = false; //alternate between player and AI
			}	
			else{
				roundChoice(true);
				starter = true;
			}
			checkState(); //check for fainted pokemon
		}
		updateState(); //update hp and energy
	}
	
	////////////ACTION SELECTION////////////
	public static void roundChoice(boolean comp){
		Pokemon chosen = comp ? enemypoke : playerpoke;
		Pokemon affected = comp ? playerpoke : enemypoke;
		gridDisplay(chosen);
		System.out.println("\n           vs.             \n");
		gridDisplay(affected);
		
		if(chosen.getState().contains("Stunned")){ //stunned pokemon cant do anything, check first to skip code
			System.out.printf("%s is stunned and cannot attack or retreat! It has passed.\n\n", chosen.getName());
			chosen.changeState("Stunned", true);
		}
		else if(comp){//AI turn
			System.out.println("It's the Gym Leader's turn!");
			ArrayList<Attack> possibleatk = new ArrayList<Attack>();
			for(int i = 0; i < chosen.getNumAtks(); i ++){
				if(chosen.getAttacks()[i].getCost() <= chosen.getEnergy()){ //checks that cost of attack is less than energy remaining 
					possibleatk.add(chosen.getAttacks()[i]); //if less, attack is possible
				}
			}
			if(possibleatk.size() > 0){
				chosen.hit(affected, possibleatk.get(rand.nextInt(possibleatk.size()))); //attacks with random possible attack
			}
			else{ //no possible attack (list of possible attacks is 0)
				System.out.println("The Gym Leader has passed."); 
			}
		}
		else{ //player turn
			boolean playergo = true;
			while(playergo){ 
				System.out.print("It's your turn!\n");
				int pchoice = Integer.parseInt(input("1. Attack    2. Retreat    3. Pass    4. View PC    5. Save and/or Quit \nI choose option #"));
				if(confirm(pchoice)){
					if(pchoice == 1){
						while(playergo){
							System.out.println("You decide to attack the enemy with your Pokemon. Pick an attack. (type 'cancel' to go back)");
							int i = 1;
							for(Attack atkchoice : chosen.getAttacks()){
								System.out.printf("%d. %-20s | Cost: %2d | Damage: %3d | Special: %-10s\n", i, atkchoice.getName(), atkchoice.getCost(), atkchoice.getDMG(), atkchoice.getSpecial());
								i ++;
							}
							String action = input(String.format("%s, use attack #", chosen.getName()));
							if(action.equals("cancel") || action.equals("")){
								break; //move back to outer loop 
							}
							int achoice = Integer.parseInt(action) - 1;
							if(achoice >= 0 && achoice < chosen.getNumAtks() && confirm(achoice + 1)){
								if(chosen.getAttacks()[achoice].getCost() <= chosen.getEnergy()){
									chosen.hit(affected, chosen.getAttacks()[achoice]);
									playergo = false; //action is done, break from all loops
								}
								else{
									System.out.printf("%s stumbles. It seems like %s doesn't have enough energy to use that attack! You should probably pick a different attack.\n", chosen.getName(), chosen.getName());
								}
							}
							else{
								System.out.println("That isn't a valid choice!");
							}
						}
					}
					else if(pchoice == 2){
						if(chosen.getHP() < 20 && rand.nextInt(10) == 0){//RNG event: if Pokemon is too low on HP, it may fail to retreat
							System.out.println("You try to retrieve your wounded Pokemon, but it is too hurt to retreat...");
						}
						else{
							System.out.println("Choose an awake Pokemon from your bag to switch with your active Pokemon.");
							retreat();
						}	
						playergo = false;
					}
					else if(pchoice == 3){
						System.out.println("You have passed your turn.");
						playergo = false;
					}
					else if(pchoice == 4){ //method of looking at fainted pokemon
						System.out.println("You load up your PC and access the Pokemon Center to view your fainted Pokémon:");
						if(graveyard.size() == 0){
							System.out.println("You have no fainted Pokémon! \n");
							break;
						}
						gridDisplay(graveyard, true);
					}
					else if(pchoice == 5){
						int endchoice = Integer.parseInt(input("1. Save & Quit   2. Quit Without Saving \nOption #"));
						if(confirm(endchoice)){
							if(endchoice == 1){
								try{
									PrintWriter fsavefile = new PrintWriter(new BufferedWriter (new FileWriter ("savefilefriendly.txt", false))); //will overwrite both save files and open new FileWriter with both files
									PrintWriter esavefile = new PrintWriter(new BufferedWriter (new FileWriter ("savefileenemy.txt", false)));
									fsavefile.println(playername);//first line in fsavefile is name of player
									
									//for current player pokemon
									String saveatk = "";
									for(Attack pokeatk : playerpoke.getAttacks()){
										saveatk += String.format(",%s,%d,%d,%s", pokeatk.getName(), pokeatk.getCost(), pokeatk.getDMG(), pokeatk.getSpecial().equals("None") ? " " : pokeatk.getSpecial()); //gets all info about attack and adds it to one string
									} //write to file with all relevant pokemon info just like pokemon.txt
									fsavefile.println(String.format("%s,%d,%s,%s,%s,%d%s", playerpoke.getName(), playerpoke.getHP(), playerpoke.getTypes()[0], playerpoke.getTypes()[1].equals("None") ? " " : playerpoke.getTypes()[1], playerpoke.getTypes()[2].equals("None") ? " " : playerpoke.getTypes()[2], playerpoke.getNumAtks(), saveatk)); 
									
									//for all friendly pokemon
									for(Pokemon fpoke : friendlies){
										saveatk = "";
										for(Attack pokeatk : fpoke.getAttacks()){
											saveatk += String.format(",%s,%d,%d,%s", pokeatk.getName(), pokeatk.getCost(), pokeatk.getDMG(), pokeatk.getSpecial().equals("None") ? " " : pokeatk.getSpecial());
										}
										String savepoke = String.format("%s,%d,%s,%s,%s,%d%s", fpoke.getName(), fpoke.getHP(), fpoke.getTypes()[0], fpoke.getTypes()[1].equals("None") ? " " : fpoke.getTypes()[1], fpoke.getTypes()[2].equals("None") ? " " : fpoke.getTypes()[2], fpoke.getNumAtks(), saveatk);
										fsavefile.println(savepoke);
									}
									
									//for current enemy pokemon
									saveatk = "";
									for(Attack pokeatk : enemypoke.getAttacks()){
										saveatk += String.format(",%s,%d,%d,%s", pokeatk.getName(), pokeatk.getCost(), pokeatk.getDMG(), pokeatk.getSpecial().equals("None") ? " " : pokeatk.getSpecial());
									}
									esavefile.println(String.format("%s,%d,%s,%s,%s,%d%s", enemypoke.getName(), enemypoke.getHP(), enemypoke.getTypes()[0], enemypoke.getTypes()[1].equals("None") ? " " : enemypoke.getTypes()[1], enemypoke.getTypes()[2].equals("None") ? " " : enemypoke.getTypes()[2], enemypoke.getNumAtks(), saveatk));
									
									//for all enemy pokemon
									for(Pokemon epoke : enemies){
										saveatk = "";
										for(Attack pokeatk : epoke.getAttacks()){
											saveatk += String.format(",%s,%d,%d,%s", pokeatk.getName(), pokeatk.getCost(), pokeatk.getDMG(), pokeatk.getSpecial().equals("None") ? " " : pokeatk.getSpecial());
										}
										String savepoke = String.format("%s,%d,%s,%s,%s,%d%s", epoke.getName(), epoke.getHP(), epoke.getTypes()[0], epoke.getTypes()[1].equals("None") ? " " : epoke.getTypes()[1], epoke.getTypes()[2].equals("None") ? " " : epoke.getTypes()[2], epoke.getNumAtks(), saveatk);
										esavefile.println(savepoke);
									}
									System.out.println("Your progress has been saved. Quitting...");
									fsavefile.close();
									esavefile.close();
									
									System.exit(0);
								}
								catch(IOException error){
									System.out.println("There was an error reading the save file. The save file may not exist in this folder. Try again or start a new game. \nError Message: " + error.getMessage());
								}
							}
							else if(endchoice == 2){
								System.out.println("Your progress has not been saved. Quitting...");
								
								System.exit(0); //quits the program
							}
						}
					}
				}
			}
		}
	}
	
	////////////INPUT COMMAND SHORTCUT////////////
	public static String input(String phrase){ //very commonly used, so method is made instead of typing excessive amounts of lines/code
		System.out.print(phrase);
		return kb.nextLine();
	}
	
	////////////GRID DISPLAY FOR INDIVIDUAL POKEMON////////////
	public static void gridDisplay(Pokemon chosenpoke){ //overload method to allow individual pokemon's stats to be displayed
		ArrayList<Pokemon> temppoke = new ArrayList<Pokemon>();
		temppoke.add(chosenpoke);
		gridDisplay(temppoke, true);
	}
	
	////////////GRID DISPLAY FOR POKEMON LISTS////////////
	public static void gridDisplay(ArrayList<Pokemon> pokelist, boolean displaystat){
		int listsize = pokelist.size();
		for(int i = 0; i < listsize; i+=4){ //7 rows of 4 pokemon each (4 x 7 = 28)
			int limit = i + 4 >= listsize ? listsize : i + 4; //prevent index out of bounds error by stopping at max list size
			for(int a = 0; a < (4 >= listsize ? listsize : 4); a++){
				System.out.print("-------------------------+");
			}
			System.out.println();
			for(int a = i; a < limit; a++){
				System.out.printf(" %2d| %-20s|", a + 1, pokelist.get(a).getName());
			}
			System.out.println();
			for(int a = i; a < limit; a++){
				System.out.printf("     Health Points: %-5s|", pokelist.get(a).getHP());
			}
			System.out.println();
			if(displaystat){ //used to display stats of pokemon after game starts
				for(int a = i; a < limit; a++){
					System.out.printf("     Energy: %-12d|", pokelist.get(a).getEnergy());
				}
				System.out.println();
				for(int a = i; a < limit; a++){
					System.out.printf("     Stunned: %-11s|", pokelist.get(a).getState().contains("Stunned") ? "Yes" : "No");
				}
				System.out.println();
				for(int a = i; a < limit; a++){
					System.out.printf("     Disabled: %-10s|", pokelist.get(a).getState().contains("Disabled") ? "Yes" : "No");
				}
				System.out.println();
			}
			else{ ///basic info for choosing initial pokemon
				for(int a = i; a < limit; a++){
					System.out.printf("     Type: %-14s|", pokelist.get(a).getTypes()[0]);
				}
				System.out.println();
				for(int a = i; a < limit; a++){
					System.out.printf("     Resistance: %-8s|", pokelist.get(a).getTypes()[1]);
				}
				System.out.println();
				for(int a = i; a < limit; a++){
					System.out.printf("     Weakness: %-10s|", pokelist.get(a).getTypes()[2]);
				}
				System.out.println();
				for(int a = i; a < limit; a++){
					System.out.printf("     %d Attack(s) %8s|", pokelist.get(a).getNumAtks(), "");
				}
				System.out.println();
			}
		}
		for(int a = 0; a < (4 >= listsize ? listsize : 4); a++){
				System.out.print("-------------------------+");
		}
		System.out.println();
	}
	
	////////////POKEMON SELECTION METHOD////////////
	public static Pokemon pickPoke(ArrayList<Pokemon> pokelist){
		int picked;
		while(true){
			picked = Integer.parseInt(input("I choose Pokemon #"));
			if(picked - 1 < pokelist.size() && picked > 0 && pokelist.get(picked - 1).getHP() > 0){ //checking if it is a valid number (in the list) and not fainted
				if(!confirm(picked)){
					continue;
				}
				break;
			}
			else{
				System.out.println("That is not a valid choice.");
			}
		}
		return pokelist.get(picked - 1); //displayed numbers start at 1, array indexes start at 0 so 1 needs to be subtracted
	}
	
	////////////CONFIRM & DOUBLE CHECK////////////
	public static boolean confirm(int choice){
		String decision = input("Are you sure you would like to pick option #" + choice + "? ('no' or 'n' to cancel)\n");
		if(decision.toLowerCase().equals("n") || decision.toLowerCase().equals("no")){ //user goes back on decision, types n or no
			return false;
		}
		return true;
	}
	
	////////////RETREATING////////////
	public static void retreat(){
		gridDisplay(friendlies, true); 			  //choose from 
		Pokemon newchoice = pickPoke(friendlies); //list of available pokemon
		System.out.printf("%s has safely retreated. I choose you, %s! \n", playerpoke.getName(), newchoice.getName());
		friendlies.add(playerpoke); //retreat current pokemon
		playerpoke = newchoice; //swap places
		friendlies.remove(playerpoke); //remove new current from available in list to get rid of duplicates
	}
	
	////////////END OF ROUND UPDATES////////////
	public static void updateState(){
		enemypoke.changeEnergy(10); //end of round energy regain
		System.out.println("All awake enemy Pokemon have recovered 10 Energy.");
		
		playerpoke.changeHP(20); //end of round HP regain
		playerpoke.changeEnergy(10);
		
		for(Pokemon friendly : friendlies){
			if(friendly.getHP() >= 0){
				friendly.changeHP(0);
			}
			else{
				graveyard.add(friendly);
				friendlies.remove(friendly);
			}
			friendly.changeEnergy(10);
		}
		System.out.println("All awake friendly Pokemon have restored 20 HP and 10 Energy. \n");
	}
	
	////////////STATE OF POKEMON UPDATES////////////
	public static void checkState(){
		if(enemypoke.getHP() <= 0){ //bad guy pokemon fainted
			System.out.printf("The Gym Leader's Pokemon, %s, has fainted!\n",enemypoke.getName());
			if(enemies.size() > 0){
				enemypoke = enemies.get(rand.nextInt(enemies.size())); //fainted is replaced by available enemy pokemon from list
				enemies.remove(enemies.indexOf(enemypoke));
				System.out.printf("'I choose you, %s!' exclaims the Gym Leader.\n\n", enemypoke.getName());
			}
			else{
				endGame("win"); //win condition: if enemy has no pokemon left
			}
		}
		
		if(playerpoke.getHP() <= 0){
			knockedOut(playerpoke);
		}
	}
	
	////////////FAINTED POKEMON////////////
	public static void knockedOut(Pokemon fainted){
		System.out.printf("%s has been knocked out! Let's see if you have any Pokemon that are still awake...\n", fainted.getName());
		graveyard.add(fainted);
		friendlies.remove(fainted);
		boolean alive = false;
		for(Pokemon friendly : friendlies){
			if(friendly.getHP() > 0){ //if any pokemon are alive, game progresses
				alive = true;
				break;
			}
		}
		
		if(alive){
			System.out.println("You fumble around in your backpack to find your awake Pokemon:");
			retreat();
		}
		else{
			endGame("loss"); //sets winner of game to enemy
		}
	}
}