import java.util.Random;
import java.util.Scanner;

public class Floor {
	private int MAP_SIZE = 4; //created in case I want to make bigger maps, in caps for easy to read format (like a constant)
	private Room[][] map = new Room[MAP_SIZE][MAP_SIZE];
	private int level;
	private int xPos;
	private int yPos;
	private int usedKey = 0;
	
	public Floor(int level) {
		int x = 0;
		int y = 0;
		
		//Generates a grid of empty rooms
		while (x < MAP_SIZE) {
			while (y < MAP_SIZE) {
				map[x][y] = new Room(x, y);
				//System.out.println("New room generated at " + x + ", " + y); //debug
				++y;
			}
			y = 0;
			++x;
		}
		//Spreads room types to rooms
		randomizeMap();
	}
	
	public void setX(int x) {
		if (x >= 0 && x < MAP_SIZE) {
			this.xPos = x;
		}
	}
	
	public void setY(int y) {
		if (y >= 0 && y < MAP_SIZE) {
			this.yPos = y;
		}
	}

	public int getX() {
		return this.xPos;
	}
	
	public int getY() {
		return this.yPos;
	}
	/**
	 * Every floor should have 1 treasure, 1 shop, 1 camp, 1 stair room and 3 item rooms
	 * Also, there is a chance for an extra treasure and 3 extra item rooms
	 * Then it will find a starting position not blocked by locked rooms
	 */
	public void randomizeMap() {
		Random rnd = new Random();
		int xTest;
		int yTest;
		int refreshFlag = 1;
		
		//Loops 12 times for 12 rooms, some required and some chance based
		while (refreshFlag > 0 && refreshFlag < 13) {
			//Each iteration chooses new coordinates
			xTest = rnd.nextInt(MAP_SIZE);
			yTest = rnd.nextInt(MAP_SIZE);
			boolean extraKey = false;
			
			switch (refreshFlag) {
			/**
			 * Every case will do the following:
			 * Check if the coordinates are empty. If not, rerun with new coords
			 * If coords are empty, fill with a room type
			 * Set the flag to the next room type
			 * "Chance" item rooms will be filled at a little over 50%
			 * 2nd treasure room will be filled at a little under 50%
			 * There will always be enough keys to unlock a floor's locked rooms
			 */
			case 1: //Adds the shop room
				if (map[xTest][yTest].getRoomType() == "empty") {
					map[xTest][yTest].setRoomType("shop");
					map[xTest][yTest].setLocked(true);
					refreshFlag++;
					System.out.println("Shop populated at " + xTest + ", " + yTest); //debug
				}
				break;
			case 2: //Adds the camp room
				if (map[xTest][yTest].getRoomType() == "empty") {
					map[xTest][yTest].setRoomType("camp");
					refreshFlag++;
					System.out.println("Camp populated at " + xTest + ", " + yTest); //debug
				}
				break;
			case 3://Adds a guaranteed treasure room
				if (map[xTest][yTest].getRoomType() == "empty") {
					map[xTest][yTest].setRoomType("treasure");
					map[xTest][yTest].setLocked(true);
					System.out.println("Treasure 1 populated at " + xTest + ", " + yTest); //debug
					refreshFlag++;
				}
				break;
			case 4: //Adds a chance to get a second treasure room
				if (map[xTest][yTest].getRoomType() == "empty" && xTest>yTest) {
					map[xTest][yTest].setRoomType("treasure");
					map[xTest][yTest].setLocked(true);
					extraKey = true;
					System.out.println("Treasure 2 populated at " + xTest + ", " + yTest); //debug
					refreshFlag++;
				} else {
					System.out.println("Treasure 2 bypassed"); //debug
					refreshFlag++;
				}
				break;
			case 5://Adds the stair room
				if (map[xTest][yTest].getRoomType() == "empty") {
					map[xTest][yTest].setRoomType("stair");
					System.out.println("Stair populated at " + xTest + ", " + yTest); //debug
					refreshFlag++;
				}
				break;
			case 6:
			case 7:
				if (map[xTest][yTest].getRoomType() == "empty") {
					map[xTest][yTest].setRoomType("item");
					map[xTest][yTest].setKey(true);
					System.out.println("Key populated at " + xTest + ", " + yTest); //debug
					refreshFlag++;
				}
				break;
			case 8:
				if (map[xTest][yTest].getRoomType() == "empty") {
					map[xTest][yTest].setRoomType("item");
					if (extraKey) {
						map[xTest][yTest].setKey(true);
					}
					System.out.println("Key populated at " + xTest + ", " + yTest); //debug
					refreshFlag++;
				}
				break;
			case 9:
			case 10:
			case 11:
				if (map[xTest][yTest].getRoomType() == "empty"&& xTest>=yTest) {
					map[xTest][yTest].setRoomType("item");
					System.out.println("Item populated at " + xTest + ", " + yTest); //debug
					refreshFlag++;
				} else {
					System.out.println("Item bypassed"); //debug
					refreshFlag++;
				}
				break;
			case 12:
				/**
				 * If the player starts in a or on the edge, and the surrounding
				 * tiles are locked, the game is soft locked. Therefore, a check
				 * is necessary before laying down the starting position. The only
				 * guaranteed path is one towards the center
				 */
				if (map[xTest][yTest].getRoomType().equals("empty")) {
					
					System.out.println("\nChecking Starting position of " + xTest + ", " + yTest); //debug
					
					if (xTest == 0) {
						if (yTest == 0 || yTest == (MAP_SIZE-1)) {
							//Corners are too high risk and require too much checking
							System.out.println("\nPosition failed as it is a corner " + xTest + ", " + yTest); //debug
							break;
						}
						if (map[xTest+1][yTest].isLocked() && (map[xTest][yTest+1].isLocked() && map[xTest][yTest-1].isLocked())) {
							System.out.println("\nPosition failed as it locked in " + xTest + ", " + yTest); //debug
							break;
						} else {
							setX(xTest);
							setY(yTest);
							System.out.println("\nStarting Pos Success, Coords: " + xTest + ", " + yTest); //debug
							refreshFlag++;
							break;
						}
					}
					
					if (xTest == (MAP_SIZE-1)) {
						if (yTest == 0 || yTest == (MAP_SIZE-1)) {
							System.out.println("\nPosition failed as it is a corner " + xTest + ", " + yTest); //debug
							break;
						}
						if (map[xTest-1][yTest].isLocked() && (map[xTest][yTest+1].isLocked() && map[xTest][yTest-1].isLocked())) {
							System.out.println("\nPosition failed as it locked in " + xTest + ", " + yTest); //debug
							break;
						}  else {
							setX(xTest);
							setY(yTest);
							System.out.println("\nStarting Pos Success, Coords: " + xTest + ", " + yTest); //debug
							refreshFlag++;
							break;
						}
					}
					
					if (yTest == 0) {
						if (map[xTest][yTest+1].isLocked() && (map[xTest+1][yTest].isLocked() && map[xTest-1][yTest].isLocked())) {
							System.out.println("\nPosition failed as it locked in " + xTest + ", " + yTest); //debug
							break;
						}  else {
							setX(xTest);
							setY(yTest);
							System.out.println("\nStarting Pos Success, Coords: " + xTest + ", " + yTest); //debug
							refreshFlag++;
							break;
						}
					}
					
					if (yTest == (MAP_SIZE-1)) {
						if (map[xTest][yTest-1].isLocked() && (map[xTest+1][yTest].isLocked() && map[xTest-1][yTest].isLocked())) {
							System.out.println("\nPosition failed as it locked in " + xTest + ", " + yTest); //debug
							break;
						}  else {
							setX(xTest);
							setY(yTest);
							System.out.println("\nStarting Pos Success, Coords: " + xTest + ", " + yTest); //debug
							refreshFlag++;
							break;
						}
					}
					//Now that we know this is not an edge case, just set the darn thing
					setX(xTest);
					setY(yTest);
					System.out.println("\nStarting Pos Success, Coords: " + xTest + ", " + yTest); //debug
					refreshFlag++;
					break;
				} else {
					System.out.println("\nPosition failed as it is not empty: " + xTest + ", " + yTest); //debug
					break;
				}
			default:
				System.out.println("\nSomething went really wrong! Probably restart your game!");
				System.out.println("\nDebug info: \nFlag: " + refreshFlag + "\nX Test: " + xTest + "\nY Test" + yTest);
				break;
			}
		}
		
	}
	
	/**
	 * Returns the map in a text visual.
	 * Has keys for each room type.
	 */
	public void visualize() {
		int x = 0;
		int y = 0;
		String test = "";
		while (x < MAP_SIZE) {
			while (y < MAP_SIZE) {
				test = map[x][y].getRoomType();
				
				if (x == xPos && y == yPos) {
					System.out.print("YOU ");
				} else if (test.equals("treasure")) {
					System.out.print("TR  ");
				} else if (test.equals("item")) {
					System.out.print("IT  ");
				} else if (test.equals("shop")) {
					System.out.print("SH  ");
				} else if (test.equals("stair")) {
					System.out.print("ST  ");
				} else if (test.equals("empty")) {
						System.out.print("EM  ");
				} else if (test.equals("camp")) {
					System.out.print("RS  ");
				}
				++y;
			}
			System.out.print("\n");
			y = 0;
			++x;
		}
	}

	//Passes room type from the current location to the game manager.
	public String roomType() {
		return map[getX()][getY()].getRoomType();
	}
	
	public boolean move(int keys) {
		String dir = "";
		@SuppressWarnings("resource")
		Scanner sc = new Scanner(System.in);
		boolean hasKey = (keys > 0);
		
		System.out.print("Enter a cardinal direction, or \"Exit\": ");
		dir = sc.nextLine();
		
		while(!dir.equalsIgnoreCase("exit")) {
			if (dir.equalsIgnoreCase("North")) { //North is negative x dir
				
				if (getX() == 0) { //First check to make sure we are within the bounds of the array
					System.out.println("There is no door to the north.");
					
				} else if (map[getX()-1][getY()].isLocked()) { //Then check if the room in that position is locked
					if (hasKey) {
						while (!dir.equals("Y") && !dir.equals("N")) {
							System.out.println("This room is locked. Would you like to use a key? (Y/N)");
							dir = sc.nextLine();
							if (dir.equals("Y")) {
								setX(xPos-1);
								setY(yPos);
								this.visualize();
								return true;
							}
						}
					} else {
						System.out.println("This room is locked, come back when you have a key.");
					}
					
				} else {
					setX(xPos-1);
					setY(yPos);
					this.visualize();
					return true;
				}
			} else if (dir.equalsIgnoreCase("South")) { //South is positive x dir
				if (getX() == MAP_SIZE-1) { //First check to make sure we are within the bounds of the array
					System.out.println("There is no door to the south.");
					
				} else if (map[getX()+1][getY()].isLocked()) { //Then check if the room in that position is locked
					if (hasKey) {
						while (!dir.equals("Y") && !dir.equals("N")) {
							System.out.println("This room is locked. Would you like to use a key? (Y/N)");
							dir = sc.nextLine();
							if (dir.equals("Y")) {
								setX(xPos+1);
								setY(yPos);
								this.visualize();
								return true;
							}
						}
					} else {
						System.out.println("This room is locked, come back when you have a key.");
					}
					
				} else {
					setX(xPos+1);
					setY(yPos);
					this.visualize();
					return true;
				}
			} else if (dir.equalsIgnoreCase("East")) { //East is positive y dir
				if (getY() == MAP_SIZE-1) { //First check to make sure we are within the bounds of the array
					System.out.println("There is no door to the east.");
					
				} else if (map[getX()][getY()+1].isLocked()) { //Then check if the room in that position is locked
					if (hasKey) {
						while (!dir.equals("Y") && !dir.equals("N")) {
							System.out.println("This room is locked. Would you like to use a key? (Y/N)");
							dir = sc.nextLine();
							if (dir.equals("Y")) {
								setX(xPos);
								setY(yPos+1);
								this.visualize();
								return true;
							}
						}
					} else {
						System.out.println("This room is locked, come back when you have a key.");
					}
					
				} else {
					setX(xPos);
					setY(yPos+1);
					this.visualize();
					return true;
				}
			} else if (dir.equalsIgnoreCase("West")) { //West is negative y dir
				if (getY() == 0) { //First check to make sure we are within the bounds of the array
					System.out.println("There is no door to the west.");
					
				} else if (map[getX()][getY()-1].isLocked()) { //Then check if the room in that position is locked
					if (hasKey) {
						while (!dir.equals("Y") && !dir.equals("N")) {
							System.out.println("This room is locked. Would you like to use a key? (Y/N)");
							dir = sc.nextLine();
							if (dir.equals("Y")) {
								setX(xPos);
								setY(yPos-1);
								this.visualize();
								return true;
							}
						}
					} else {
						System.out.println("This room is locked, come back when you have a key.");
					}
					
				} else {
					setX(xPos);
					setY(yPos-1);
					this.visualize();
					return true;
				}
			} else {
				System.out.println("Please specify a direction (North, East, South, West)");
			}
			
			System.out.println("Enter another direction? (Y) Hit any other key for No.");
			dir = sc.nextLine();
			
			if(!dir.equalsIgnoreCase("y")) {
				dir = "exit";
			}
			
		}
		
		return false;			
		
	}

	//This is probably bad code, but this helps tell if the player has used a key
	//since move() already returns a boolean
	public int usedKey() {
		int result = this.usedKey;
		this.usedKey = 0;
		return result;
	}
	
	
	//Getter/setters
	public int getMAP_SIZE() {
		return MAP_SIZE;
	}

	public void setMAP_SIZE(int mAP_SIZE) {
		MAP_SIZE = mAP_SIZE;
	}

	public Room[][] getMap() {
		return map;
	}

	public void setMap(Room[][] map) {
		this.map = map;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}
	
	
}
