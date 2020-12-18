package com.mygdx.game;

import java.util.Random;

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
		boolean extraKey = false;
		
		//Loops 12 times for 12 rooms, some required and some chance based
		while (refreshFlag > 0 && refreshFlag < 13) {
			//Each iteration chooses new coordinates
			xTest = rnd.nextInt(MAP_SIZE);
			yTest = rnd.nextInt(MAP_SIZE);
			
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
					//System.out.println("Shop populated at " + xTest + ", " + yTest); //debug
				}
				break;
			case 2: //Adds the camp room
				if (map[xTest][yTest].getRoomType() == "empty") {
					map[xTest][yTest].setRoomType("camp");
					refreshFlag++;
					//System.out.println("Camp populated at " + xTest + ", " + yTest); //debug
				}
				break;
			case 3://Adds a guaranteed treasure room
				if (map[xTest][yTest].getRoomType() == "empty") {
					map[xTest][yTest].setRoomType("treasure");
					map[xTest][yTest].setLocked(true);
					//System.out.println("Treasure 1 populated at " + xTest + ", " + yTest); //debug
					refreshFlag++;
				}
				break;
			case 4: //Adds a chance to get a second treasure room
				if (map[xTest][yTest].getRoomType() == "empty" && xTest>yTest) {
					map[xTest][yTest].setRoomType("treasure");
					map[xTest][yTest].setLocked(true);
					extraKey = true;
					//System.out.println("Treasure 2 populated at " + xTest + ", " + yTest); //debug
					refreshFlag++;
				} else {
					//System.out.println("Treasure 2 bypassed"); //debug
					refreshFlag++;
				}
				break;
			case 5://Adds the stair room
				if (map[xTest][yTest].getRoomType() == "empty") {
					map[xTest][yTest].setRoomType("stair");
					//System.out.println("Stair populated at " + xTest + ", " + yTest); //debug
					refreshFlag++;
				}
				break;
			case 6: //Adds keys in item rooms
			case 7:
				if (map[xTest][yTest].getRoomType() == "empty") {
					map[xTest][yTest].setRoomType("item");
					map[xTest][yTest].setKey(true);
					//System.out.println("Key populated at " + xTest + ", " + yTest); //debug
					refreshFlag++;
				}
				break;
			case 8: //Adds a third key if needed, and an item
				if (map[xTest][yTest].getRoomType() == "empty") {
					map[xTest][yTest].setRoomType("item");
					if (extraKey) {
						map[xTest][yTest].setKey(true);
						//System.out.println("Key populated at " + xTest + ", " + yTest); //debug
					} else {
						//System.out.println("Key bypassed at " + xTest + ", " + yTest); //debug
					}
					refreshFlag++;
				}
				break;
			case 9: //Three possible additional item rooms
			case 10:
			case 11:
				if (map[xTest][yTest].getRoomType() == "empty"&& xTest>=yTest) {
					map[xTest][yTest].setRoomType("item");
					//System.out.println("Item populated at " + xTest + ", " + yTest); //debug
					refreshFlag++;
				} else {
					//System.out.println("Item bypassed"); //debug
					refreshFlag++;
				}
				break;
			case 12: //Sets beginning position.
				/**
				 * If the player starts in a or on the edge, and the surrounding
				 * tiles are locked, the game is soft locked. Therefore, a check
				 * is necessary before laying down the starting position. The only
				 * guaranteed path is one towards the center
				 */
				if (map[xTest][yTest].getRoomType().equals("empty")) {
					
					//System.out.println("\nChecking Starting position of " + xTest + ", " + yTest); //debug
					
					if (xTest == 0) {
						if (yTest == 0 || yTest == (MAP_SIZE-1)) {
							//Corners are too high risk and require too much checking
							//System.out.println("\nPosition failed as it is a corner " + xTest + ", " + yTest); //debug
							break;
						}
						if (map[xTest+1][yTest].isLocked() && (map[xTest][yTest+1].isLocked() && map[xTest][yTest-1].isLocked())) {
							//System.out.println("\nPosition failed as it locked in " + xTest + ", " + yTest); //debug
							break;
						} else {
							setX(xTest);
							setY(yTest);
							//System.out.println("\nStarting Pos Success, Coords: " + xTest + ", " + yTest); //debug
							//Prevents players from being attacked in the first room
							roomEncountered();
							refreshFlag++;
							break;
						}
					}
					
					if (xTest == (MAP_SIZE-1)) {
						if (yTest == 0 || yTest == (MAP_SIZE-1)) {
							//System.out.println("\nPosition failed as it is a corner " + xTest + ", " + yTest); //debug
							break;
						}
						if (map[xTest-1][yTest].isLocked() && (map[xTest][yTest+1].isLocked() && map[xTest][yTest-1].isLocked())) {
							//System.out.println("\nPosition failed as it locked in " + xTest + ", " + yTest); //debug
							break;
						}  else {
							setX(xTest);
							setY(yTest);
							//System.out.println("\nStarting Pos Success, Coords: " + xTest + ", " + yTest); //debug
							roomEncountered();
							refreshFlag++;
							break;
						}
					}
					
					if (yTest == 0) {
						if (map[xTest][yTest+1].isLocked() && (map[xTest+1][yTest].isLocked() && map[xTest-1][yTest].isLocked())) {
							//System.out.println("\nPosition failed as it locked in " + xTest + ", " + yTest); //debug
							break;
						}  else {
							setX(xTest);
							setY(yTest);
							//System.out.println("\nStarting Pos Success, Coords: " + xTest + ", " + yTest); //debug
							roomEncountered();
							refreshFlag++;
							break;
						}
					}
					
					if (yTest == (MAP_SIZE-1)) {
						if (map[xTest][yTest-1].isLocked() && (map[xTest+1][yTest].isLocked() && map[xTest-1][yTest].isLocked())) {
							//System.out.println("\nPosition failed as it locked in " + xTest + ", " + yTest); //debug
							break;
						}  else {
							setX(xTest);
							setY(yTest);
							//System.out.println("\nStarting Pos Success, Coords: " + xTest + ", " + yTest); //debug
							roomEncountered();
							refreshFlag++;
							break;
						}
					}
					//Now that we know this is not an edge case, just set the darn thing
					setX(xTest);
					setY(yTest);
					//System.out.println("\nStarting Pos Success, Coords: " + xTest + ", " + yTest); //debug
					roomEncountered();
					refreshFlag++;
					break;
				} else {
					//System.out.println("\nPosition failed as it is not empty: " + xTest + ", " + yTest); //debug
					break;
				}
			default:
				System.out.println("\nSomething went really wrong! Probably restart your game!");
				System.out.println("\nDebug info: \nFlag: " + refreshFlag + "\nX Test: " + xTest + "\nY Test" + yTest);
				break;
			}
		}
		
	}
	
	public boolean checkLock(int dir) {
			if (dir == -1 && getX() != 0) { //North is negative x dir
				if (map[getX()-1][getY()].isLocked()) { //Then check if the room in that position is locked
					return true;
				} else {
						return false;
				}
					
			} else if (dir == 1 && getX() != MAP_SIZE-1) { //South is positive x dir
				if (map[getX()+1][getY()].isLocked()) { //Then check if the room in that position is locked
					return true;
				} else {
						return false;
				}
				
			} else if (dir == 2 && getY() != MAP_SIZE-1) { //East is positive y dir
				if (map[getX()][getY()+1].isLocked()) { //Then check if the room in that position is locked
					return true;
				} else {
						return false;
				}
				
			} else if (dir == -2 && getY() != 0) { //West is negative y dir
				if (map[getX()][getY()-1].isLocked()) { //Then check if the room in that position is locked
					return true;
				} else {
						return false;
				}
			}
		
		return false;			
		
	}
	
	public void move(int dir) {
		switch (dir) {
		case 0: //North
			xPos--;
			break;
		case 1: //South
			xPos++;
			break;
		case 2: //East
			yPos++;
			break;
		case 3: //West
			yPos--;
			break;
		}
	}
	
	public boolean roomEncountered() {
		return (map[xPos][yPos].hadEncounter());
	}
	
	public void setRoomEnounter() {
		map[xPos][yPos].setEncounter(true);
	}

	public int getMAP_SIZE() {
		return MAP_SIZE;
	}

	public void setMAP_SIZE(int mAP_SIZE) {
		MAP_SIZE = mAP_SIZE;
	}
	
	public void loot() {
		map[xPos][yPos].setRoomType("empty");
	}
	
	public int ferryRoomCode() {
		return map[xPos][yPos].roomCode();
	}
	
	public void unlock() {
		map[xPos][yPos].setLocked(false);
	}
	
}
