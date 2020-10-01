import java.util.Random;
import java.util.Scanner;

//This is to test that my git commits are actually working.

public class GameManager {
	private int level = 0;
	private Player player;
	private Floor activeFloor;
	private boolean inCombat = false;
	private boolean canRest = true;
	private boolean defending = false;
	private int defendCooldown = 0;
	boolean enemyTurn = true;
	boolean combatInit = false;
	
	public GameManager(Player player){
		setPlayer(player);
		System.out.println("Player loaded into game successfully."); //debug
		System.out.println("If you need help at any point, type \"Help\" or \"Controls\".");
		
		setLevel(player.getMaxLevel());
		System.out.println("You start on level " + getLevel());
		setActiveFloor(new Floor(getLevel()));
		getActiveFloor().visualize();
	}
	
	public void playGame() {
		@SuppressWarnings("resource")
		Scanner playerInput = new Scanner(System.in);
		String gameControl = "";
		Enemy monster = new Enemy(getLevel());
		
		//This is it, this is the game part. Where you play the game. 
		while (!gameControl.equalsIgnoreCase("exit")) {
			
			
			if (combatInit) {
				monster = new Enemy(getLevel());
				setInCombat(true);
				if (monster.getSpeed() > player.getSpeed()) {
					System.out.println("Your opponent is faster than you!");
					this.enemyTurn = true;
				} else {
					System.out.println("You outspeed your opponent!");
					this.enemyTurn = false;
				}
				this.combatInit = false;
			}
			
			if(inCombat) { //Starts the combat sequence
				/**
				 * Because I didn't want to write options again in a combat
				 * method, I have to repeat some of the combat code for two
				 * cases, one where the player is faster and one where the
				 * enemy is faster. Inefficient, but a finished game is better
				 * than no game.
				 */
				if (enemyTurn) {
					if (!monster.isStunned()) { //Checks to see if the monster is stunned, if yes unstun, if no it can attack.
							if (!defending) { //If the player defended in their last move.
								System.out.println("Your opponent attacks!");
								player.damage(monster.getAttack());
								this.enemyTurn = false;
							} else {
								System.out.println("You blocked the attack, and stunned your enemy!");
								this.defending = false;
								monster.setStunned(true);
								enemyTurn = false;
							}
					} else {
						System.out.println("Your opponent is still reeling from your block.");
						enemyTurn = false;
					}
				}
			}
			
			System.out.print("Input your next action: ");
			gameControl = playerInput.nextLine();
			
			if(gameControl.equalsIgnoreCase("help") || gameControl.equalsIgnoreCase("controls")) {
				helpList();
			} else if (gameControl.equalsIgnoreCase("stats") ) {
				player.printStats();
				
			} else if (gameControl.equalsIgnoreCase("look") ) {
				if (inCombat) {
					System.out.println("Your enemy has " + monster.getHealth() + "/" + monster.getMaxHealth() + " health");
				}
				
				System.out.println(getActiveFloor().passDescription());
				
			} else if (gameControl.equalsIgnoreCase("exit") ) {
				//offer to save
				
			} else if (gameControl.equalsIgnoreCase("save") ) {
				System.out.println("Are you sure you want to overwrite your file? You cannot undo this action. (Y/N)");
				while(!gameControl.equalsIgnoreCase("y") && !gameControl.equalsIgnoreCase("n")) {
					gameControl = playerInput.nextLine();
					if (gameControl.equalsIgnoreCase("y")) {
							player.savePlayer();
					}
				}
				
				
			} else if (gameControl.equalsIgnoreCase("attack") ) {
				if(!inCombat) {
					System.out.println("You cannot perform that action here.");
				} else {
					if(defendCooldown>0) { //Defending is on a 4 turn cool down, to prevent abuse
						defendCooldown--;
					}
					if(monster.damage(player.getAttack())) {
						Random coinAmount = new Random();
						int coins = coinAmount.nextInt(4+getLevel()*2);
						System.out.println("You have defeated your foe and gained " + coins + " coins");
						player.setMoney(player.getMoney()+coins);
						setInCombat(false);
					} else {
						System.out.println("You strike your enemy.");
						this.enemyTurn = true;
					}
				}
				
			} else if (gameControl.equalsIgnoreCase("defend") ) {
				if(!inCombat) {
					System.out.println("You cannot perform that action here.");
				} else {
					if (this.defendCooldown > 0) {
						System.out.println("You can't defend for " + this.defendCooldown + " turns");
					} else {
						this.defendCooldown = 4;
						this.defending = true;
						this.enemyTurn = true;
					}
					
				}
				
			} else if (gameControl.equalsIgnoreCase("item") ) {
				//bring up an item menu
				
			} else if (gameControl.equalsIgnoreCase("move") ) {
				if(inCombat) {
					System.out.println("You are under attack! Defeat your foe before you move on.");
				} else {
					if(getActiveFloor().move(player.getKeys())) {
						Random fightChance = new Random();
						int fightPercent = fightChance.nextInt(100);
						
						if(getActiveFloor().usedKey() == 1) {
							player.setKeys(player.getKeys() - 1);
							System.out.println("You used a key. You have " + player.getKeys() + " remaining");
						}
						
						if(getActiveFloor().roomType().equals("empty") && !getActiveFloor().roomEncountered()) {
							if(fightPercent <= 50) {
								this.combatInit = true;
								System.out.println("You're under attack!");
							} 
						} else if(getActiveFloor().roomType().equalsIgnoreCase("item") && !getActiveFloor().roomEncountered()) {
							if(fightPercent <= 33) {
								this.combatInit = true;
								System.out.println("You're under attack!");
							}
							
						}
						
						getActiveFloor().setRoomEnounter();
					}
				}
				
			} else if (gameControl.equalsIgnoreCase("loot") ) {
				if(inCombat) {
					System.out.println("You are under attack! Defeat your foe before you look for items.");
				} else {
					//add item to player's inventory
				}
				
			} else if (gameControl.equalsIgnoreCase("rest") ) {
				if (getActiveFloor().roomType().equals("camp") && isCanRest()){
					System.out.println("You lay down to rest for a moment. Your hp has been restored.");
					player.setHealth(player.getHealth()+20);
					setCanRest(false);
				} else if (!getActiveFloor().roomType().equals("camp")) {
					System.out.println("This is no place for a nap.");
				} else {
					System.out.println("You have already rested here, it's time to get going.");
				}
				
			} else if (gameControl.equalsIgnoreCase("shop") ) {
				if (getActiveFloor().roomType().equals("shop")) {
					//open shop
				} else {
					System.out.println("There is no shop here...");
				}
			} else if (gameControl.equalsIgnoreCase("onward") ) {
				if (getActiveFloor().roomType().equals("stair")) {
					System.out.println("Are you sure? You will permanently increase the difficulty and never be able to revisit this floor.");
					System.out.print("Press \"Y\" to confirm, anything else to cancel.");
					
					gameControl = playerInput.nextLine();
					if(gameControl.equalsIgnoreCase("y")) {
						System.out.println("You venture onward.");
						setLevel(getLevel()+1);
						setActiveFloor(new Floor(getLevel()));
						setCanRest(true);
						player.setMaxLevel(getLevel());
					}
				}
			} else {
				System.out.println("Please input a valid action. If you would like a list, type \"Help\" or \"Controls\"");
			}
		}
		
		
	}
	
	public void helpList() {
		System.out.println("**********Controls**********");
		System.out.println("Help:   Use at any time to bring up this list.");
		System.out.println("Stats:  Use at any time to check your player stats.");
		System.out.println("Look:   Use at any time to get a description of the room you're in.");
		System.out.println("Exit:   Use at any time to exit to the main menu.");
		System.out.println("Save:   Use at any time to save your game.");
		System.out.println("Attack: Use in combat to damage your enemy.");
		System.out.println("Defend: Use in combat to block the next attack completely.");
		System.out.println("Item:   Use in or out of combat to pull up a list of items to use.");
		System.out.println("Move:   Use when not in combat to move to an adjacent room. Will prompt for a direction.");
		System.out.println("Loot:   Use when not in combat in a room with items to pick up.");
		System.out.println("Rest:   Use when you find a camp room to heal yourself.");
		System.out.println("Shop:   Use when in a shop room to access the shop.");
		System.out.println("Onward: Use when in a room with stairs to venture to the next floor.");
		return;
	}

	
	
//	public void debug( ) {
//		Scanner stop = new Scanner(System.in);
//		Floor currentFloor = new Floor(level);
//		currentFloor.visualize();
//		String stopIt = stop.nextLine();
//		System.out.println("\nTest over.");
//	}


	public int getLevel() {
		return level;
	}


	public void setLevel(int level) {
		this.level = level;
	}


	public Player getPlayer() {
		return player;
	}


	public void setPlayer(Player player) {
		this.player = player;
	}

	public Floor getActiveFloor() {
		return activeFloor;
	}

	public void setActiveFloor(Floor activeFloor) {
		this.activeFloor = activeFloor;
	}

	public boolean isInCombat() {
		return inCombat;
	}

	public void setInCombat(boolean inCombat) {
		this.inCombat = inCombat;
	}

	public boolean isCanRest() {
		return canRest;
	}

	public void setCanRest(boolean canRest) {
		this.canRest = canRest;
	}
	
	
	
}
