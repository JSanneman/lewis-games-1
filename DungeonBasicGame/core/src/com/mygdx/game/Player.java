package com.mygdx.game;

import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Polygon;

import edu.lewisu.cs.cpsc41000.common.Boundary;
import edu.lewisu.cs.cpsc41000.common.EdgeHandler;
import edu.lewisu.cs.cpsc41000.common.ImageBasedScreenObject;
import edu.lewisu.cs.cpsc41000.common.ImageBasedScreenObjectDrawer;
import edu.lewisu.cs.cpsc41000.common.MobileImageBasedScreenObject;
import edu.lewisu.cs.cpsc41000.common.labels.ActionLabel;
import edu.lewisu.cs.cpsc41000.common.labels.SoundLabel;
import edu.lewisu.cs.cpsc41000.common.motioncontrollers.Tracker;

/**
 * Player classes are the program's copy of a player file.
 * They open the file(or create it) and load the data from that file for use in game.
 * The player object has values for everything the user can obtain or change about
 * their player. It is used only by the Game Manager
 */

public class Player extends MobileImageBasedScreenObject{
	
	public Player(Texture tex, int xpos, int ypos, int xorigin, int yorigin, int rotation, int scaleX, int scaleY,
			boolean flipX, boolean flipY, float frameWidth, float frameHeight, int[] frameSequence, float animDelay) {
		super(tex, xpos, ypos, xorigin, yorigin, rotation, scaleX, scaleY, flipX, flipY, frameWidth, frameHeight, frameSequence,
				animDelay);
	}

	public Player(Texture img, int i, int j, boolean b) {
		super(img, i, j, b);
	}

	//The game needs easy access to all stats, so a player class will have all on hand.
	private int maxLevel = 1;
	private int maxHealth = 20;
	private int health = 20;
	private int attack = 5;
	private int defense = 3;
	private int speed = 1;
	private int keys = 0;
	private int money = 0;
	private int bombs = 0;
	private int healthPotions = 0;
	private String playerFileName = "";
	
	@Override
    public void initBoundingPolygon() { //Creates an octogon with turned so points are on top/bottom/etc
		float d = super.getWidth();
		float r = d/2;
		float diag = (float) (Math.sqrt(2)/2);
		float a = r*(1-diag);
		float b = a + r;
		
        float[] vertices = new float[16];
        vertices[0] = r;
        vertices[1] = 0;
        vertices[2] = b;
        vertices[3] = a;
        vertices[4] = d;
        vertices[5] = r;
        vertices[6] = b;
        vertices[7] = b;
        vertices[8] = r;
        vertices[9] = d;
        vertices[10] = a;
        vertices[11] = b;
        vertices[12] = 0;
        vertices[13] = r;
        vertices[14] = a;
        vertices[15] = a;
        boundingPolygon = new Polygon(vertices);
    }
    
	
	/**
	 * Takes in a player file to alter stats
	 * Will not take in blank or mismatched files
	 * @param fileName Used to load the file without using the private string for.. unknown reasons
	 * @return will return the status of the load sequence
	 */
	public boolean loadPlayer(String fileName) {
		File playerFile = new File(fileName + ".txt");
		try {
			Scanner fileIn = new Scanner(playerFile);
			//Player file will be extremely basic and contain this order.
			//@adding items
			setMaxLevel(fileIn.nextInt());
			setMaxHealth(fileIn.nextInt());
			setHealth(fileIn.nextInt());
			setAttack(fileIn.nextInt());
			setDefense(fileIn.nextInt());
			setSpeed(fileIn.nextInt());
			setKeys(fileIn.nextInt());
			setMoney(fileIn.nextInt());
			setBombs(fileIn.nextInt());
			setHealthPotions(fileIn.nextInt());
			fileIn.close();
			return true;
		} catch (Exception ex) { //Helpful for accidental empty files. 
			System.out.println(ex);
			return false;
		}
		
	}
	
	/**
	 * This method creates a temporary file, attempts to delete the existing file,
	 * then attempts to restore the file with new data and delete the temporary file.
	 * Probably the most complicated process, if this gets interrupted it tried to leave
	 * the user with a temporary file to re-use on another player.
	 * @return will return if the player has saved. 
	 */
	public boolean savePlayer() {
		File tempFile = new File("tempPlayer.txt");
		File oldFile = new File(playerFileName + ".txt");
		
		try {
			if (!tempFile.createNewFile()) {
				System.out.println("Sorry, but there is currently a temporary file lingering from a previous failed save. Please address that before creating a new game.");
				return false;
			}
		} catch (Exception ex) {
			System.out.println(ex);
			return false;
		}
		
		
		try {
			FileWriter tempPlayer = new FileWriter(tempFile);
			//Couldn't be bothered to type " " 14 times
			String sp = " ";
			
			//@adding items
			//Player file will start a new line and save all the current loaded data.
			tempPlayer.write(getMaxLevel() + sp + getMaxHealth() + sp + getHealth() + sp + getAttack() + sp + getDefense()
								+ sp + getSpeed() + sp + getKeys() + sp + getMoney() + sp + getBombs() + sp + getHealthPotions());
			
			//System.out.println("\nTemporary file has been populated"); //debug
			
			if (oldFile.delete()) {
				
				//System.out.println("\nOld file successfully deleted."); //debug
				File saveFile = new File(playerFileName + ".txt");
				
				if (saveFile.createNewFile()) {
					//System.out.println("\nNew file successfully created."); //debug
				} else {
					//System.out.println("\nNew file was not created."); //debug
				}
				
				tempPlayer.close();
				
				try {
					FileWriter savePlayer = new FileWriter(saveFile);
					//@adding items
					savePlayer.write(getMaxLevel() + sp + getMaxHealth() + sp + getHealth() + sp + getAttack() + sp + getDefense()
					+ sp + getSpeed() + sp + getKeys() + sp + getMoney() + sp + getBombs() + sp + getHealthPotions());
					
					//System.out.println("\nNew file saved successfully."); //debug
					
					if (tempFile.delete()) {
						//System.out.println("\nTemporary file deleted successfully."); //debug
					} else {
						//System.out.println("\nTemporary file was not deleted, somehow."); //debug
					}
					
					savePlayer.close();
					return true;
				
				} catch (Exception ex) {
					System.out.println(ex);
					return false;
				}
				
				
			} else {
				System.out.println("\nSomething has gone wrong, unable to save.\n In the event that your old file no longer works, try typing in \"tempPlayer\" after choosing \"Load\".");
				tempPlayer.close();
			}
			
		} catch(Exception ex) {
			System.out.println(ex);
			return false;
		}

		return false;
	}

	
	public void printStats() { //@adding items
		System.out.println("Your health is at: " + getHealth() + "/" + getMaxHealth());
		System.out.println("Your attack is:    " + getAttack());
		System.out.println("Your defense is:   " + getDefense());
		System.out.println("Your speed is:     " + getSpeed());
		System.out.println("You have           " + getMoney() + " coins.");
		System.out.println("You have           " + getKeys() + " keys.");
		System.out.println("You have           " + getBombs() + " bombs.");
		System.out.println("You have           " + getHealthPotions() + " health potions.");
	}
	
	//Easier to use and to read than get/set
	public void heal(int amount) {
		setHealth(getHealth() + amount);
	}
	
	//When a player the file is lost forever
	public void deletePlayerFile() {
		File thePoorSoul = new File(playerFileName + ".txt");
		if(thePoorSoul.delete()) {
			System.out.println("The poor soul is lost forever");
		} else {
			System.out.println("You have beaten death. Please stop doing that.");
		}
	}
	
	//returns whether or not the player died
	public boolean damage(int amount) {
		if (amount > getDefense()) {
			setHealth(getHealth() - (amount - getDefense()));
			System.out.println("You took " + (amount - getDefense()) + " damage");
		} else {
			setHealth(getHealth() - 1);
			System.out.println("You took 1 damage");
		}
		if (getHealth()<0) {
			return true;
		} else {
			return false;
		}
	}
	

	public int getMaxLevel() {
		return maxLevel;
	}

	public void setMaxLevel(int maxLevel) {
		this.maxLevel = maxLevel;
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

	//Health can't go over max health, issue a check here
	public void setHealth(int health) {
		if (health > getMaxHealth()) {
			this.health = getMaxHealth();
		} else {
			this.health = health;
		}
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

	public int getSpeedStat() {
		return speed;
	}

	public void setSpeedStat(int speed) {
		this.speed = speed;
	}

	public int getKeys() {
		return keys;
	}

	public void setKeys(int keys) {
		this.keys = keys;
	}
	
	public void lootKey() {
		this.keys++;
	}
	
	public void useKey() {
		this.keys--;
	}

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		this.money = money;
	}
	
	public void lootMoney(int money) {
		this.money += money;
	}
	
	public void useMoney(int money) {
		this.money -= money;
	}

	public int getBombs() {
		return bombs;
	}

	public void setBombs(int bombs) {
		this.bombs = bombs;
	}
	
	public void lootBomb() {
		this.bombs++;
	}
	
	public void useBomb() {
		this.bombs--;
	}

	
	public String getPlayerFileName() {
		return playerFileName;
	}

	
	public void setPlayerFileName(String playerFileName) {
		this.playerFileName = playerFileName;
	}

	public int getHealthPotions() {
		return healthPotions;
	}

	public void setHealthPotions(int healthPotions) {
		this.healthPotions = healthPotions;
	}

}
