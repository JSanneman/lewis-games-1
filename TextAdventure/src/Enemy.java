import java.util.Random;

/**
 * Enemy classes are similar to player classes, but are only used in combat.
 * They have randomized stats to add a small amount of uniqueness.
 * This object does not interact with any files and is called from Game Manager.
 */

public class Enemy {
	private int difficulty;
	private int maxHealth = 16;
	private int health = 16;
	private int attack = 0;
	private int defense = -2;
	private int speed = -1;
	private String eType = "basic";
	private boolean stunned = false;
	
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
			setMaxHealth(getMaxHealth() + rolled*(getDifficulty()+5/5));
			setHealth(getMaxHealth());
		} else {
			setMaxHealth(getMaxHealth() - rolled*(getDifficulty()+5/5));
			setHealth(getMaxHealth());
		}
		
		rolled = roll.nextInt(2);
		sign = roll.nextInt(2);
		if (sign == 0) {
			setAttack(getAttack() + rolled*(getDifficulty()+5/5));
		} else {
			setAttack(getAttack() - rolled*(getDifficulty()+5/5));
		}
		
		rolled = roll.nextInt(2);
		sign = roll.nextInt(2);
		if (sign == 0) {
			setDefense(getDefense() + rolled*(getDifficulty()+5/5));
		} else {
			setDefense(getDefense() - rolled*(getDifficulty()+5/5));
		}
		
		rolled = roll.nextInt(2);
		sign = roll.nextInt(2);
		if (sign == 0) {
			setSpeed(getSpeed() + rolled*(getDifficulty()+5/5));
		} else {
			setSpeed(getSpeed() - rolled*(getDifficulty()+5/5));
		}
		
	}
	
	
	
	//A stunned enemy will not be able to attack, but will not be stunned for the next turn
	public boolean isStunned() {
		if (this.stunned) {
			this.stunned = false;
			return true;
		} else {
			return false;
		}
	}

	public void setStunned(boolean stunned) {
		this.stunned = stunned;
	}

	public void heal(int amount) {
		setHealth(getHealth() + amount);
	}
	
	public boolean damage(int amount) {
		if (amount > getDefense()) {
			setHealth(getHealth() - (amount - getDefense()));
			System.out.println("Your enemy took " + (amount - getDefense()) + " damage");
		} else {
			setHealth(getHealth() - 1);
			System.out.println("Your enemy took 1 damage");
		}
		if (getHealth()<=0) {
			return true;
		} else {
			return false;
		}
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
