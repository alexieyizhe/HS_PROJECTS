public class Attack{
	private String atkname;
	private int cost;
	private int dmg;
	private String special;
		
	public Attack(String name, int cost, int damage, String special){
		this.atkname = name; //name of attack
		this.cost = cost; //cost of attack
		this.dmg = damage; //amount of damage the attack does
		this.special = special; //special ability of attack
	}
	
	public String getName(){
		return atkname;
	}
	
	public int getCost(){
		return cost;
	}
	
	public int getDMG(){
		return dmg;
	}
	
	public String getSpecial(){
		return special;
	}
	
}