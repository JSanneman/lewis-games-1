
/**
 * Room objects are used to keep track of room types, in order to randomize floors.
 * Each room has to keep track of its type and if it has been traveled through.
 * It will also know if it's locked, if it has been unlocked, and if it has a key.
 * All of the information it has is used to keep the floors random.
 * The room class is called by the Floor class, but ferries some commands
 * directly to the Game Manager with methods in the Floor class.
 */

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
		decodeDescription();
		return roomType;
	}

	public void setRoomType(String roomType) {
		decodeDescription();
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
	public void decodeDescription() {
		if (this.roomType == "treasure") {
			this.description = "\nThis room contains a treasure! Type \"Loot\" to pick it up!";
		} else if (this.roomType == "item") {
			this.description = "\nThis room contains a useful item! Type \"Loot\" to pick it up!";
		} else if (this.roomType == "shop") {
			this.description = "\nA friendly shopkeeper is here. Type \"Shop\" to check their stock!";
		} else if (this.roomType == "stair") {
			this.description = "\nThere are some stairs here. You can't see much. Type \"Onward\" to go down a floor.\nYOU CANNOT RETURN TO A PREVIOUS FLOOR";
		} else if (this.roomType == "empty") {
			this.description = "\nThere is nothing here but the walls of stone. Best be moving on.";
		} else if (this.roomType == "camp") {
			this.description = "\nLooks like you can camp here for a bit. Type \"Rest\" to take a break.\nThis will regain some health, but can only be used once.";
		} else {
			this.description = "\nIt seems this room is an enigma. This message should never be seen.";
		}
	}
	
}
