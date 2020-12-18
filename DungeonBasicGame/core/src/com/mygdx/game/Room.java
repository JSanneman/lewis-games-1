package com.mygdx.game;

public class Room {
	private String roomType = "empty";
	private String description;
	private int x;
	private int y;
	//These two are used to ensure the entire map is playable
	private boolean locked = false;
	private boolean hasKey = false;
	//This makes every room capable of spawning only one enemy.
	private boolean hadEncounter = false;
	
	// Room is initialized with it's position, because ?? I made it that way
	public Room(int x, int y) {
		setX(x);
		setY(y);
	}
 
	public String getRoomType() {
		return roomType;
	}

	public void setRoomType(String roomType) {
		this.roomType = roomType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public boolean hasKey() {
		return hasKey;
	}

	public void setKey(boolean key) {
		this.hasKey = key;
	}
	
	public boolean hadEncounter() {
		return hadEncounter;
	}

	public void setEncounter(boolean hadEncounter) {
		this.hadEncounter = hadEncounter;
	}

	// Takes room type to set a string to return when entering a room.
	public int roomCode() {
		if (this.roomType == "treasure") {
			return 0;
		} else if (this.roomType == "item") {
			if (this.hasKey) {
				return 1;
			} else {
				return 2;
			}
		} else if (this.roomType == "shop") {
			return 3;
		} else if (this.roomType == "stair") {
			return 4;
		} else if (this.roomType == "empty") {
			return 5;
		} else if (this.roomType == "camp") {
			return 6;
		} else {
			return 666;
		}
	}
	
}
