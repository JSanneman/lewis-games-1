import java.util.Random;
import java.util.Scanner;

/**
 * This is it, this is the game. Handles all of the functions needed to actually play.
 *	Called by the Adventure App, calls Floor, Player, and Enemy objects.
 */

public class GameManager {
	private int level = 0;
	private Player player;
	private Floor activeFloor;
	private boolean inCombat = false; //state of combat
	private boolean canRest = true; //limits resting to once a floor
	private boolean defending = false;
	private int defendCooldown = 0;
	private int bombCooldown = 0;
	boolean enemyTurn = true; //used for turn based combat
	boolean combatInit = false; //starts combat sequence
	
	public GameManager(Player player){ //Player file gets sorted into the game
		setPlayer(player);
		//System.out.println("Player loaded into game successfully."); //debug
		System.out.println("If you need help at any point, type \"Help\" or \"Controls\".");
		
		setLevel(player.getMaxLevel());
		System.out.println("You start on level " + getLevel());
		setActiveFloor(new Floor(getLevel()));
		//getActiveFloor().visualize(); //debug
	}
	
	public void playGame() { //The majority of the game happens here
		@SuppressWarnings("resource")
		Scanner playerInput = new Scanner(System.in);
		String gameControl = "";
		Enemy monster = new Enemy(getLevel());
		
		//This is it, this is the game part. Where you play the game. 
		while (!gameControl.equalsIgnoreCase("exit")) {
			
			
			if (combatInit) {
				monster = new Enemy(getLevel());
				setInCombat(true);
				this.defendCooldown = 0;
				this.bombCooldown = 0;
				//Decides if the player or the enemy will go first
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
				if (enemyTurn) { //Turn based combat
					if (!monster.isStunned()) { //Checks to see if the monster is stunned, if yes unstun, if no it can attack.
							if (!defending) { //If the player defended in their last move.
								System.out.println("Your opponent attacks!");
								if (player.damage(monster.getAttack())) {
									System.out.println("Game Over!");
									System.out.println("You made it to level " + getLevel());
									System.out.println(player.getPlayerFileName() + " will be erased");
									player.deletePlayerFile();
									return;
								}
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
			} else {
				//Shows the map when not in combat to reduce spam
				getActiveFloor().visualize();
			}
			
			System.out.print("Input your next action: ");
			gameControl = playerInput.nextLine();
			
			if(gameControl.equalsIgnoreCase("help") || gameControl.equalsIgnoreCase("controls")) { //Prints all the available commands
				helpList();
			} else if (gameControl.equalsIgnoreCase("stats") ) { //Allows user to see their stats
				player.printStats();
				
			} else if (gameControl.equalsIgnoreCase("look") ) { //Gives user information about their enemy and/or position
				if (inCombat) {
					System.out.println("Your enemy has " + monster.getHealth() + "/" + monster.getMaxHealth() + " health");
				}
				
				System.out.println(getActiveFloor().passDescription());
				
			} else if (gameControl.equalsIgnoreCase("exit") ) { //Leaves the current game, offers to save the player first
				System.out.println("Would you like to save first? (Y/N)");
				while(!gameControl.equalsIgnoreCase("y") && !gameControl.equalsIgnoreCase("n")) {
					gameControl = playerInput.nextLine();
					if (gameControl.equalsIgnoreCase("y")) {
							player.savePlayer();
					}
				}
				gameControl = "exit";
				
			} else if (gameControl.equalsIgnoreCase("save") ) { //Writes current loaded Player object to a text file for future use
				System.out.println("Are you sure you want to overwrite your file? You cannot undo this action. (Y/N)");
				while(!gameControl.equalsIgnoreCase("y") && !gameControl.equalsIgnoreCase("n")) {
					gameControl = playerInput.nextLine();
					if (gameControl.equalsIgnoreCase("y")) {
							player.savePlayer();
					}
				}
				
				
			} else if (gameControl.equalsIgnoreCase("attack") ) { //Uses attack stat to detract from enemy health stat
				if(!inCombat) {
					System.out.println("You cannot perform that action here.");
				} else {
					if(defendCooldown>0) { //Defending is on a 4 turn cool down, to prevent abuse
						defendCooldown--;
					}
					if(monster.damage(player.getAttack())) {
						Random coinAmount = new Random();
						int coins = coinAmount.nextInt(4+getLevel()*2); //Always a possibility to get no coins
						System.out.println("You have defeated your foe and gained " + coins + " coins");
						player.setMoney(player.getMoney()+coins);
						setInCombat(false); //Exits combat
					} else {
						System.out.println("You strike your enemy.");
						this.enemyTurn = true;
					}
				}
				
			} else if (gameControl.equalsIgnoreCase("defend") ) { //Gives the player a free hit every 4 turns, basically
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
				//Because using an item counts as a turn in combat, separate into combat/non combat uses
				if(inCombat) {
					System.out.println("Using an item in combat will count as a turn!");
					while(!gameControl.equalsIgnoreCase("n")) {
						System.out.println("You have " + player.getBombs() + " bombs available.");
						System.out.println("You have " + player.getHealthPotions() + " health potions available.");
						System.out.println("Type \"Bomb\" or \"Potion\" to use that item, or \"N\" to escape");
						gameControl = playerInput.nextLine();
						if (gameControl.equalsIgnoreCase("bomb")) {
							
							if (bombCooldown == 0) {
								
								if (player.getBombs()>0) { //Bombs do double damage but are limited on turns
									System.out.println("You used a bomb for massive damage!");
									
									if(monster.damage(player.getAttack()*2)) {
										Random coinAmount = new Random();
										int coins = coinAmount.nextInt(4+getLevel()*2);
										System.out.println("You have defeated your foe and gained " + coins + " coins");
										player.setMoney(player.getMoney()+coins);
										setInCombat(false);
									} else {
										this.enemyTurn = true;
									}
									//Ends the loop so that the enemy can get their turn
									gameControl = "n";
								} else {
									System.out.println("You don't have any bombs!");
								}
							} else {
								System.out.println("You can't use another bomb for " + bombCooldown + " turns.");
							}
						} else if (gameControl.equalsIgnoreCase("potion")) { //Heals for a fourth of total health to keep usability
							if (player.getHealthPotions()>0) {
								System.out.println("You healed for " + player.getMaxHealth()/4 + " health!");
								player.heal(player.getMaxHealth()/4);
								this.enemyTurn = true;
								//Ends the loop so that the enemy can get their turn
								gameControl = "n";
							} else {
								System.out.println("You don't have any potions!");
							}

						} else if (gameControl.equalsIgnoreCase("exit")) {
							//exits out without saying anything
						}
						
					}
					
				} else {
					while(!gameControl.equalsIgnoreCase("n")) { //Player can't use bombs outside of combat
						System.out.println("You have " + player.getBombs() + " bombs available.");
						System.out.println("You have " + player.getHealthPotions() + " health potions available.");
						System.out.println("Type \"Bomb\" or \"Potion\" to use that item, or \"N\" to escape");
						gameControl = playerInput.nextLine();
						
						if (gameControl.equalsIgnoreCase("bomb")) {
							System.out.println("You can't use a bomb when not in combat. You could hurt yourself.");
						} else if (gameControl.equalsIgnoreCase("potion")) {
							if (player.getHealthPotions()>0) {
								System.out.println("You healed for " + player.getMaxHealth()/4 + " health!");
								player.heal(player.getMaxHealth()/4);
							} else {
								System.out.println("You don't have any potions!");
							}

						}
						
					}
				}
				
			} else if (gameControl.equalsIgnoreCase("move") ) { //The most annoying command of all, controls movement and combat encounters
				if(inCombat) {
					System.out.println("You are under attack! Defeat your foe before you move on.");
				} else {
					if(getActiveFloor().move(player.getKeys())) { //Calls the Floor to see if player was able to move
						Random fightChance = new Random();
						int fightPercent = fightChance.nextInt(100);
						
						if(getActiveFloor().usedKey() == 1) { //Checks if the player used a key while changing rooms
							player.setKeys(player.getKeys() - 1);
							System.out.println("You used a key. You have " + player.getKeys() + " remaining");
						}
						
						if(getActiveFloor().roomType().equals("empty") && !getActiveFloor().roomEncountered()) { //50% chance to fight an enemy in an empty room
							if(fightPercent < 50) {
								this.combatInit = true;
								System.out.println("You're under attack!");
							} 
						} else if(getActiveFloor().roomType().equalsIgnoreCase("item") && !getActiveFloor().roomEncountered()) { //34% chance to fight an enemy in an item room
							if(fightPercent <= 33) {
								this.combatInit = true;
								System.out.println("You're under attack!");
							}
							
						}
						
						//Tells the room that the player has been here, as to not have a combat chance here again.
						getActiveFloor().setRoomEnounter();
					}
				}
				
			} else if (gameControl.equalsIgnoreCase("loot") ) { //Allows player to pick up items and treasure
				if(inCombat) {
					System.out.println("You are under attack! Defeat your foe before you look for items.");
				} else {
					//Simple way to get a random chance out of 100
					Random lootRoll = new Random();
					int lootChance = lootRoll.nextInt(100);
					
					if(getActiveFloor().roomType().equals("item")) { //Awards items for combat and keys.
						if (getActiveFloor().keyRoom()) { //Key rooms will give keys in addition to items
							System.out.println("You found a key! This probably unlocks something somewhere.");
							player.setKeys(player.getKeys() +1);
						} else {
							if (lootChance < 50) {
								System.out.println("You picked up a health potion! Drink at any time to regain health.");
								player.setHealthPotions(player.getHealthPotions() +1);
							} else if (lootChance >= 50) {
								System.out.println("You picked up a bomb! Use during combat to deal massive damage.");
								player.setBombs(player.getBombs() +1);
							}
							//Room will be emptied only after the non-key item is taken, and will show as empty on the map
							getActiveFloor().emptyRoom();
						}
							
					} else if(getActiveFloor().roomType().equals("treasure")) { //Awards stat boosts as treasure
						//Basic amulets have a 22% chance, with a 13% chance for a boost to all stats
						if (lootChance < 21){
							System.out.println("You found an amulet of health! You gain a boost of 10 hp!");
							player.setMaxHealth(player.getMaxHealth()+10);
							player.heal(10);
						} else if (lootChance < 43) {
							System.out.println("You found an amulet of attack! You gain a boost of 5 attack!");
							player.setAttack(player.getAttack()+5);
						} else if (lootChance < 65) {
							System.out.println("You found an amulet of defense! You gain a boost of 5 defense!");
							player.setDefense(player.getDefense()+5);
						} else if (lootChance < 87) {
							System.out.println("You found an amulet of speed! You gain a boost of 5 speed!");
							player.setSpeed(player.getSpeed()+5);
						} else {
							System.out.println("You found a legendary amulet! All stats get a boost!");
							player.setMaxHealth(player.getMaxHealth()+8);
							player.heal(8);
							player.setAttack(player.getAttack()+4);
							player.setDefense(player.getDefense()+4);
							player.setSpeed(player.getSpeed()+4);
						}
						//Treasure rooms will be emptied once looted and show up as such on the map
						getActiveFloor().emptyRoom();
					} else {
						System.out.println("This room has no treasure to plunder.");
					}
				}
				
			} else if (gameControl.equalsIgnoreCase("rest") ) { //Allows the player to rest and gain leveled health once per floor.
				if (getActiveFloor().roomType().equals("camp") && isCanRest()){
					System.out.println("You lay down to rest for a moment. Your hp has been restored.");
					player.heal(20+getLevel()*2);
					setCanRest(false);
				} else if (!getActiveFloor().roomType().equals("camp")) {
					System.out.println("This is no place for a nap.");
				} else {
					System.out.println("You have already rested here, it's time to get going.");
				}
				
			} else if (gameControl.equalsIgnoreCase("shop") ) { //Opens the shop menu.
				if (getActiveFloor().roomType().equals("shop")) {
					shopMenu();
				} else {
					System.out.println("There is no shop here...");
				}
			} else if (gameControl.equalsIgnoreCase("onward") ) { //Sends the player forward in the game.
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


	//Brings up a menu, separate from the main selections
	public void shopMenu() {
		String input = "";
		@SuppressWarnings("resource")
		Scanner playerIn = new Scanner(System.in);
		//The player quickly becomes overpowered without some sort of hindrance to their ability to level stats.
		int costMultiplier = level/2;
		
		System.out.println("The shop keeper welcomes you to their store. You're directed to a list of their wares:");
		
		while(!input.equalsIgnoreCase("exit")) {
			//Shows accurate pricing information based on level
			System.out.println("Item:            Cost:    \"Type me!\"");
			System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
			System.out.println("Bombs:           " + (3+costMultiplier) + " gold   \"Bomb\"   ");
			System.out.println("Health Potions:  " + (3+costMultiplier) + " gold   \"Potion\" ");
			System.out.println("Health Boost(7): " + (6+2*+costMultiplier) + " gold   \"Health\" ");
			System.out.println("Attack Boost(5): " + (6+2*+costMultiplier) + " gold   \"Attack\" ");
			System.out.println("Defense Boost(5):" + (6+2*+costMultiplier) + " gold   \"Defense\"");
			System.out.println("Speed Boost(5):  " + (5+2*+costMultiplier) + " gold   \"Speed\"  ");
			System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
			System.out.println("You have " + player.getMoney() + " gold.");
			
			System.out.println("\nWhat would you like to purchase? \"Exit\" to exit the shop");
			input = playerIn.nextLine();
			
			//Every case will catch if the player doesn't have enough money, and subtract that amount if they do
			
			if (input.equalsIgnoreCase("bomb")) {
				if (player.getMoney()<3+costMultiplier) {
					System.out.println("It looks like you don't have enough money for this item.");
				} else {
					player.setMoney(player.getMoney() - 3+costMultiplier);
					player.setBombs(player.getBombs()+1);
					System.out.println("Bomb purchased!");
				}
			} else if (input.equalsIgnoreCase("potion")) {
				if (player.getMoney()<3+costMultiplier) {
					System.out.println("It looks like you don't have enough money for this item.");
				} else {
					player.setMoney(player.getMoney() - 3+costMultiplier);
					player.setHealthPotions(player.getHealthPotions()+1);
					System.out.println("Health Potion purchased!");
				}
			} else if (input.equalsIgnoreCase("health")) {
				if (player.getMoney()<6+2*costMultiplier) {
					System.out.println("It looks like you don't have enough money for this item.");
				} else {
					player.setMoney(player.getMoney() - 6+2*+costMultiplier);
					player.setMaxHealth(player.getMaxHealth()+7);
					player.heal(7);
					System.out.println("Health Boost purchased!");
				}
			} else if (input.equalsIgnoreCase("attack")) {
				if (player.getMoney()<6+2*costMultiplier) {
					System.out.println("It looks like you don't have enough money for this item.");
				} else {
					player.setMoney(player.getMoney() - 6+2*costMultiplier);
					player.setAttack(player.getAttack()+5);
					System.out.println("Attack Boost purchased!");
				}
			} else if (input.equalsIgnoreCase("defense")) {
				if (player.getMoney()<6+2*costMultiplier) {
					System.out.println("It looks like you don't have enough money for this item.");
				} else {
					player.setMoney(player.getMoney() - 6+2*costMultiplier);
					player.setDefense(player.getDefense()+5);
					System.out.println("Defense Boost purchased!");
				}
			} else if (input.equalsIgnoreCase("speed")) {
				if (player.getMoney()<5+2*costMultiplier) {
					System.out.println("It looks like you don't have enough money for this item.");
				} else {
					player.setMoney(player.getMoney() - 5+2*costMultiplier);
					player.setSpeed(player.getSpeed()+5);
					System.out.println("Speed Boost purchased!");
				}
			} else if (input.equalsIgnoreCase("exit")) {
				//Just skips without the "that's not a command" warning
			} else {
				System.out.println("Please select an item or exit the shop.");
			}
		}
		System.out.println("The shopkeeper wishes you luck, and waves goodbye.");
		
	}

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
