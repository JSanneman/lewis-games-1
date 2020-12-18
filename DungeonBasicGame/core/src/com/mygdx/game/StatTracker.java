//Interfaces with all game stats and the application

package com.mygdx.game;

import java.security.SecureRandom;

public class StatTracker {
	private int level;
	//private int xPos, yPos;
	private Floor activeFloor;
	protected boolean inCombat;
	//private boolean canRest;
	//private boolean defending; //maybe remove
	private Player player;
	SecureRandom battle = new SecureRandom();
	
	protected boolean showKey, showChest, showCampfire, showShop, showStairs, showAmulet, showNull;
	
	public StatTracker(Player player) {
		this.player = player;
		level = player.getMaxLevel();
		setActiveFloor(new Floor(getLevel()));
	}

	public int getLevel() {
		return level;
	}

	public int xPos() {
		return activeFloor.getX();
	}
	
	public String xString() {
		return (""+activeFloor.getX());
	}
	
	public int yPos() {
		return activeFloor.getY();
	}
	
	public String yString() {
		return (""+activeFloor.getY());
	}
	
	public void setLevel(int level) {
		this.level = level;
	}

	public Floor getActiveFloor() {
		return activeFloor;
	}

	public void setActiveFloor(Floor activeFloor) {
		this.activeFloor = activeFloor;
	}
	
	public boolean setLeftDoor() {
		if (activeFloor.getY() == 0) {
			return false;
		} else {
			return true;
		}
	}
	
	public boolean setRightDoor() {
		if (activeFloor.getY() == activeFloor.getMAP_SIZE()-1) {
			return false;
		} else {
			return true;
		}
	}
	
	public boolean setTopDoor() {
		if (activeFloor.getX() == 0) {
			return false;
		} else {
			return true;
		}
	}
	
	public boolean setBottomDoor() {
		if (activeFloor.getX() == activeFloor.getMAP_SIZE()-1) {
			return false;
		} else {
			return true;
		}
	}
	
	public boolean topLock() {
		return activeFloor.checkLock(-1);
	}
	public boolean bottomLock() {
		return activeFloor.checkLock(1);
	}
	public boolean leftLock() {
		return activeFloor.checkLock(-2);
	}
	public boolean rightLock() {
		return activeFloor.checkLock(2);
	}
	
	public void moveTop(float ypos) {
		activeFloor.move(0);
		updateRoom();
		player.setYPos(ypos);
	}
	
	public void moveBottom(float ypos) {
		activeFloor.move(1);
		updateRoom();
		player.setYPos(ypos);
	}
	
	public void moveRight(float xpos) {
		activeFloor.move(2);
		updateRoom();
		player.setXPos(xpos);
	}
	
	public void moveLeft(float xpos) {
		activeFloor.move(3);
		updateRoom();
		player.setXPos(xpos);
	}
	
	public void updateRoom() {
		resetRoomInfo();
		activeFloor.unlock();
		switch (activeFloor.ferryRoomCode()) {
		case 0: //Treasure (amulet)
			this.showAmulet = true;
			break;
		case 1: //Key
			if (!activeFloor.roomEncountered()) {
				battleStance(1);
			}
			this.showKey = true;
			break;
		case 2: //Chest
			if (!activeFloor.roomEncountered()) {
				battleStance(1);
			}
			this.showChest = true;
			break;
		case 3: //Shop
			this.showShop = true;
			break;
		case 4: //Stair
			this.showStairs = true;
			break;
		case 5: //Empty
			this.showNull = true;
			if (!activeFloor.roomEncountered()) {
				battleStance(2);
			}
			break;
		case 6: //Campfire
			this.showCampfire = true;
			break;
		case 666: //What even
			break;
		}
		
		activeFloor.setRoomEnounter();
	}
	
	public void wipeItem() {
		activeFloor.loot();
		updateRoom();
	}
	
	public void resetRoomInfo() {
		this.showAmulet = false;
		this.showCampfire = false;
		this.showChest = false;
		this.showKey = false;
		this.showShop = false;
		this.showStairs = false;
	}
	
	public void advanceFloor() {
		this.level++;
		this.activeFloor = new Floor(level);
		this.activeFloor.setRoomEnounter();
		this.updateRoom();
	}
	
	public void battleStance(int chance) {
		if (battle.nextInt(chance) == 0) {
			this.inCombat = true;
		}
	}
	
	public void endBattle() {
		this.inCombat = false;
	}
}
