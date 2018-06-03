import java.util.*;
public class Pokemon{
	private final int POKENAME = 0;
	private final int HP = 1; //amount of hp points remaining
	private final int SPECIES = 2;
	private final int RESIST = 3;
	private final int WEAK = 4;
	private final int NUMATKS = 5; //number of attacks available
	
	private String name;
	private int hp;
	private int maxhp;
	private String type;
	private String resistance;
	private String weakness;
	private int numattacks;
	private Attack[] attacks;
	private int energy = 50; //energy level of current pokemon
	private ArrayList<String> state = new ArrayList<String>(); //special conditions, i.e. poisoned
	private static Random rand = new Random();
	
	public Pokemon(String stats){ //line of all stats of pokemon except energy, state, and active is passed in
		String[] statlist = stats.split(",");
		
		name = statlist[POKENAME];
		hp = Integer.parseInt(statlist[HP]);
		maxhp = hp;
		type = statlist[SPECIES];
		resistance = statlist[RESIST].equals(" ") ? "None" : statlist[RESIST]; //None if no resistance on pokemon
		weakness = statlist[WEAK].equals(" ") ? "None" : statlist[WEAK]; //None if no weakness on pokemon
		numattacks = Integer.parseInt(statlist[NUMATKS]);
		Attack[] pokeatks = new Attack[numattacks];
		
		for(int i = 0; i < pokeatks.length; i ++){
			pokeatks[i] = new Attack(statlist[6 + i * 4], Integer.parseInt(statlist[7 + i * 4]), Integer.parseInt(statlist[8 + i * 4]), statlist[9 + i * 4].equals(" ") ? "None" : statlist[9 + i * 4]); //Creating new attack class with 4 given information
		}
		attacks = pokeatks;
	}
	
	public String getName(){
		return name;
	}
	
	public int getHP(){
		return hp;
	}
	
	public int getmaxHP(){
		return maxhp;
	}
	
	public String[] getTypes(){
		return new String[]{type, resistance, weakness};
	}
	
	public int getNumAtks(){
		return numattacks;
	}
	
	public Attack[] getAttacks(){
		return attacks;
	}
	
	public ArrayList<String> getState(){
		return state;
	}
	
	public int getEnergy(){
		return energy;
	}
	
	public void hit(Pokemon damagedpoke, Attack atk){
		int dmgdone = atk.getDMG() * -1;
											//halve damage if opponent pokemon has resistance			//double damage if opponent pokemon has weakness
		if(state.contains("Disabled") && dmgdone != 0){ //prevents 0 damage attacks from gaining damage when taking 10 dmg penalty while disabled
			dmgdone += 10;
		}
		String enemyname = damagedpoke.getName();
		String specialtype = atk.getSpecial();
		
		//checking resistances
		if(damagedpoke.getTypes()[1].equals(getTypes()[0])){ //resistance equal to type
			dmgdone /= 2;
			System.out.printf("%s has a resistance to %s; all damage dealt will be halved! \n", enemyname, type);
		}
		else if(damagedpoke.getTypes()[2].equals(getTypes()[0])){ //weakness equal to type
			dmgdone *= 2;
			System.out.printf("%s is %s's weakness! All damage dealt will be doubled. \n", type, enemyname);
		}	
																																																//dmg done is negative number, have to plus to subtract
		String attacktxt = String.format("%s has attacked %s with %s! It dealt %d damage, reducing %s's health to %d.", name, enemyname, atk.getName(), dmgdone * -1, enemyname, damagedpoke.getHP() + dmgdone < 0 ? 0 : damagedpoke.getHP() + dmgdone);
		int success = rand.nextInt(2); //determines if 50% chance specials succeed
		if(specialtype.equals("Wild Card")){
			if(success == 1){
				damagedpoke.changeHP(dmgdone);
				System.out.println(attacktxt);
			}
			else{
				System.out.printf("%s's attack with the Wild Card special has missed!\n", name);
			}
		}
		else if(specialtype.equals("Wild Storm")){
			while(success == 1 && damagedpoke.getHP() > 0){ //keeps going if attack keeps succeeding
				success = rand.nextInt(2); //rolls dice again for next attack success or not
				damagedpoke.changeHP(dmgdone);
				attacktxt = String.format("%s has attacked %s with %s! It dealt %d damage, reducing %s's health to %d.", name, enemyname, atk.getName(), dmgdone * -1, enemyname, damagedpoke.getHP());
				System.out.println(attacktxt);
				System.out.printf("%s has a Wild Storm special, meaning %s keep attacking until it misses!\n", atk.getName(), name); 
			}
			System.out.printf("%s's attack with the Wild Storm special has missed!\n", name);
		}
		else if(specialtype.equals("Recharge")){
			changeEnergy(20);
			System.out.printf("%s has used Recharge. It has recovered 20 energy.\n", name);
		}
		else if(specialtype.equals("Disable")){
			damagedpoke.changeState("Disabled", false);
			damagedpoke.changeHP(dmgdone);
			System.out.println(attacktxt);
			System.out.printf("%s has been disabled by %s's attack! All of its attacks will do 10 less damage for the rest of this battle.\n", enemyname, name);
		}
		else{ //normal attack & stun attack
			damagedpoke.changeHP(dmgdone);
			System.out.println(attacktxt);
			if(specialtype.equals("Stun")){
				if(success == 1){
					damagedpoke.changeState("Stunned", false);
					System.out.printf("%s has been stunned by %s's attack! It will not be able to retreat or attack on its next turn. \n", enemyname, name);
				}
				else{
					System.out.printf("%s's Stun attack has missed! The opponent pokemon will not be affected by the special effect. \n", name);
				}
			}
		}
		changeEnergy(atk.getCost() * -1);
		System.out.printf("%s has spent %d energy on the attack. It has %d energy left.\n\n", name, atk.getCost(), energy);
	}
		
	public void changeHP(int hpdiff){
		hp = hp + hpdiff < 0 ? 0 : hp + hpdiff; //min hp is 0
		hp = hp > maxhp ? maxhp : hp; //cannot go higher than maxhp
	}	
	
	public void changeEnergy(int energydiff){
		energy = energy + energydiff < 0 ? 0 : energy + energydiff; //min energy is 0
		energy = energy > 50 ? 50 : energy; //max energy is 50
	}
	
	public void changeState(String condition, boolean remove){
		if(remove){ //condition wearing off
			state.remove(condition);
		}
		else{//applying condition
			state.add(condition);
		}
	}
}