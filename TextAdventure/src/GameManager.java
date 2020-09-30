import java.util.Scanner;

//This is to test that my git commits are actually working.

public class GameManager {
	private int level = 0;
	private Player player;
	private Floor activeFloor;
	private boolean inCombat = false;
	private boolean canRest = true;
	
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
		
		
		//This is it, this is the game part. Where you play the game. 
		while (!gameControl.equalsIgnoreCase("exit")) {
			System.out.print("Input your next action: ");
			gameControl = playerInput.nextLine();
			
			if(gameControl.equalsIgnoreCase("help") || gameControl.equalsIgnoreCase("controls")) {
				helpList();
			} else if (gameControl.equalsIgnoreCase("stats") ) {
				player.printStats();
				
			} else if (gameControl.equalsIgnoreCase("look") ) {
				//Call description
				
			} else if (gameControl.equalsIgnoreCase("exit") ) {
				//offer to save
				
			} else if (gameControl.equalsIgnoreCase("save") ) {
				//save the game
				
			} else if (gameControl.equalsIgnoreCase("attack") ) {
				if(!inCombat) {
					System.out.println("You cannot perform that action here.");
				} else {
					//use stats to detract health
				}
				
			} else if (gameControl.equalsIgnoreCase("defend") ) {
				if(!inCombat) {
					System.out.println("You cannot perform that action here.");
				} else {
					//nullify the next attack, raise attack for next turn
				}
				
			} else if (gameControl.equalsIgnoreCase("item") ) {
				//bring up an item menu
				
			} else if (gameControl.equalsIgnoreCase("move") ) {
				if(inCombat) {
					System.out.println("You are under attack! Defeat your foe before you move on.");
				} else {
					if(getActiveFloor().move(player.getKeys())) {
						//trigger potential enemy attack
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
					
					gameControl = playerInput.next();
					if(gameControl.equalsIgnoreCase("y")) {
						System.out.println("You venture onward.");
						setLevel(getLevel()+1);
						setActiveFloor(new Floor(getLevel()));
						setCanRest(true);
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
