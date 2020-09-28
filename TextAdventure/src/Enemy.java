import java.util.Random;

public class Enemy {
	private int difficulty;
	private int maxHealth = 16;
	private int health = 16;
	private int attack = 0;
	private int defense = -2;
	private int speed = -1;
	private String eType = "basic";
	
	public Enemy(int level) {
		//Enemy initialized with leveled stats based on what floor they spawn on.
		setDifficulty(level);
		setMaxHealth(getMaxHealth() + 4*getDifficulty());
		setHealth(getMaxHealth());
		setAttack(getAttack() + 2*getDifficulty());
		setDefense(getDefense() + 2*getDifficulty());
		setSpeed(getSpeed() + 2*getDifficulty());
		rollStats();
	}
	
	/**
	 * Stats are randomized to add flavor to the game.
	 * Health will go up or down in increments of 0 to 4.
	 * Attack, defense, and speed will have increments of 0 to 2.
	 * Increments are multiplied by one extra ever five floors.
	 */
	public void rollStats() {
		Random roll = new Random();
		int rolled;
		int sign;
		
		
		rolled = roll.nextInt(4);
		sign = roll.nextInt(2);
		if (sign == 0) {
			setMaxHealth(getMaxHealth() + rolled*(getDifficulty()/5));
			setHealth(getMaxHealth());
		} else {
			setMaxHealth(getMaxHealth() - rolled*(getDifficulty()/5));
			setHealth(getMaxHealth());
		}
		
		rolled = roll.nextInt(2);
		sign = roll.nextInt(2);
		if (sign == 0) {
			setAttack(getAttack() + rolled*(getDifficulty()/5));
		} else {
			setAttack(getAttack() - rolled*(getDifficulty()/5));
		}
		
		rolled = roll.nextInt(2);
		sign = roll.nextInt(2);
		if (sign == 0) {
			setDefense(getDefense() + rolled*(getDifficulty()/5));
		} else {
			setDefense(getDefense() - rolled*(getDifficulty()/5));
		}
		
		rolled = roll.nextInt(2);
		sign = roll.nextInt(2);
		if (sign == 0) {
			setSpeed(getSpeed() + rolled*(getDifficulty()/5));
		} else {
			setSpeed(getSpeed() - rolled*(getDifficulty()/5));
		}
		
		//TODO add enemy types list (warrior, rogue, etc)
		
	}
	
	
	public int getDifficulty() {
		return difficulty;
	}
	public void setDifficulty(int difficulty) {
		this.difficulty = difficulty;
	}
	public int getMaxHealth() {
		return maxHealth;
	}
	public void setMaxHealth(int maxHealth) {
		this.maxHealth = maxHealth;
	}
	public int getHealth() {
		return health;
	}
	public void setHealth(int health) {
		this.health = health;
	}
	public int getAttack() {
		return attack;
	}
	public void setAttack(int attack) {
		this.attack = attack;
	}
	public int getDefense() {
		return defense;
	}
	public void setDefense(int defense) {
		this.defense = defense;
	}
	public int getSpeed() {
		return speed;
	}
	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public String geteType() {
		return eType;
	}

	public void seteType(String eType) {
		this.eType = eType;
	}
	
	
}
