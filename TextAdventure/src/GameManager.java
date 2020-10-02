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
	private int bombCooldown = 0;
	boolean enemyTurn = true;
	boolean combatInit = false;
	
	public GameManager(Player player){
		setPlayer(player);
		//System.out.println("Player loaded into game successfully."); //debug
		System.out.println("If you need help at any point, type \"Help\" or \"Controls\".");
		
		setLevel(player.getMaxLevel());
		System.out.println("You start on level " + getLevel());
		setActiveFloor(new Floor(getLevel()));
		//getActiveFloor().visualize(); //debug
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
				if (enemyTurn) {
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
				System.out.println("Would you like to save first? (Y/N)");
				while(!gameControl.equalsIgnoreCase("y") && !gameControl.equalsIgnoreCase("n")) {
					gameControl = playerInput.nextLine();
					if (gameControl.equalsIgnoreCase("y")) {
							player.savePlayer();
					}
				}
				gameControl = "exit";
				
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
								
								if (player.getBombs()>0) {									
									System.out.println("You used a bomb for massive damage!");
									
									if(monster.damage(monster.getMaxHealth()/2)) {
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
						} else if (gameControl.equalsIgnoreCase("potion")) {
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
					while(!gameControl.equalsIgnoreCase("n")) {
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
					Random lootRoll = new Random();
					int lootChance = lootRoll.nextInt(100);
					
					if(getActiveFloor().roomType().equals("item")) {
						if (getActiveFloor().keyRoom()) {
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
							getActiveFloor().emptyRoom();
						}
							
					} else if(getActiveFloor().roomType().equals("treasure")) {
						if (lootChance < 22){
							System.out.println("You found an amulet of health! You gain a boost of 10 hp!");
							player.setMaxHealth(player.getMaxHealth()+10);
							player.heal(10);
						} else if (lootChance < 44) {
							System.out.println("You found an amulet of attack! You gain a boost of 5 attack!");
							player.setAttack(player.getAttack()+5);
						} else if (lootChance < 66) {
							System.out.println("You found an amulet of defense! You gain a boost of 5 defense!");
							player.setDefense(player.getDefense()+5);
						} else if (lootChance < 88) {
							System.out.println("You found an amulet of speed! You gain a boost of 5 speed!");
							player.setSpeed(player.getSpeed()+5);
						} else {
							System.out.println("You found a legendary amulet! All stats get a boost!");
							player.setMaxHealth(player.getMaxHealth()+5);
							player.heal(5);
							player.setAttack(player.getAttack()+3);
							player.setDefense(player.getDefense()+3);
							player.setSpeed(player.getSpeed()+3);
						}
						getActiveFloor().emptyRoom();
					} else {
						System.out.println("This room has no treasure to plunder.");
					}
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
					shopMenu();
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


	public void shopMenu() {
		String input = "";
		@SuppressWarnings("resource")
		Scanner playerIn = new Scanner(System.in);
		int costMultiplier = level/3;
		
		System.out.println("The shop keeper welcomes you to their store. You're directed to a list of their wares:");
		
		while(!input.equalsIgnoreCase("exit")) {
			System.out.println("Item:            Cost:    \"Type me!\"");
			System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
			System.out.println("Bombs:           3 gold   \"Bomb\"   ");
			System.out.println("Health Potions:  3 gold   \"Potion\" ");
			System.out.println("Health Boost(7): 6 gold   \"Health\" ");
			System.out.println("Attack Boost(5): 6 gold   \"Attack\" ");
			System.out.println("Defense Boost(5):6 gold   \"Defense\"");
			System.out.println("Speed Boost(5):  3 gold   \"Speed\"  ");
			System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
			System.out.println("You have " + player.getMoney() + " gold.");
			
			System.out.println("\nWhat would you like to purchase? \"Exit\" to exit the shop");
			input = playerIn.nextLine();
			
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
					System.out.println("Potion purchased!");
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
		System.out.println("The shopkeeper wished you luck, and waves goodbye");
		
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
