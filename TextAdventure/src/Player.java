import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

public class Player {
	//The game needs easy access to all stats, so a player class will have all on hand.
	private int maxLevel = 1;
	private int maxHealth = 20;
	private int health = 20;
	private int attack = 1;
	private int defense = 0;
	private int speed = 1;
	private int keys = 0;
	private int money = 0;
	private int bombs = 0;
	private String playerFileName = "";
	
	/**
	 * This will in-take a new file name to start a new character with base stats.
	 * If character already exists, it will load it with loadPlayer
	 * These will both check for temporary file names in case a save sequence goes south.
	 * Requires user to change file names in illegal cases. 
	 * @param fileName
	 */
	public Player(String fileName ) {
		Scanner scan = new Scanner(System.in);
		try {
			File newPlayer = new File(fileName + ".txt");
			if (newPlayer.createNewFile()) {
				
				if (fileName.equals("tempPlayer")) {
					System.out.println("There is no temporary file to be loaded.");
					scan.close();
					newPlayer.delete();
					return;
				}
				
				System.out.println("\nFile created. To load this character in the future, enter their name: " + fileName);
				setPlayerFileName(fileName);
				scan.close();
			} else {
				System.out.println("\nFile loaded. Welcome " + fileName);
				
				//Handles empty or mismatched files
				if (!loadPlayer(fileName)) {
					System.out.println("Loading sequence failed. Check to see if your file is empty or missing data.");
					return;
				}
				
				//Handles illegal name uses
				if (fileName.equals("tempPlayer") || fileName.equals("emergencyTempPlayer")) {
					while (fileName.equals("tempPlayer") || fileName.equals("emergencyTempPlayer")) {
						System.out.print("\nPlease rename your file, that name is designated for temporary files: ");
						fileName = scan.nextLine();
					}
					//Creates a new file for the saving sequence.
					File newerPlayer = new File(fileName + ".txt");
					if (newerPlayer.createNewFile()) {
						System.out.println("New file " + fileName + ".txt created.");
					} else { //If the user puts in an already existing file, it will save over that file, so they are given the opportunity to abort save.
						System.out.println("File " + fileName + ".txt already exists! Type \"Y\" to continue, any other character to abort.");
						String abort = scan.next();
						if (!abort.equalsIgnoreCase("y")) {
							System.out.println("Your file was not overwritten, and the temporary file still exists.");
							scan.close();
							return;
						}
						
						System.out.println(fileName + ".txt will be deleted. Your temporary data will be permanently stored under the same name.");
					}
					
					if (newPlayer.delete()) {
						System.out.println("Temporary file deleted. ");
					}
				}
				
				
				setPlayerFileName(fileName);
				scan.close();
			}
		} catch (Exception ex) {
			System.out.println(ex);
			scan.close();
		}
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
			setMaxLevel(fileIn.nextInt());
			setMaxHealth(fileIn.nextInt());
			setHealth(fileIn.nextInt());
			setAttack(fileIn.nextInt());
			setDefense(fileIn.nextInt());
			setSpeed(fileIn.nextInt());
			setKeys(fileIn.nextInt());
			setMoney(fileIn.nextInt());
			setBombs(fileIn.nextInt());
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
			
			//Player file will start a new line and save all the current loaded data.
			tempPlayer.write(getMaxLevel() + sp + getMaxHealth() + sp + getHealth() + sp + getAttack() + sp + getDefense()
								+ sp + getSpeed() + sp + getKeys() + sp + getMoney() + sp + getBombs());
			
			System.out.println("\nTemporary file has been populated"); //debug
			
			if (oldFile.delete()) {
				
				System.out.println("\nOld file successfully deleted."); //debug
				File saveFile = new File(playerFileName + ".txt");
				
				if (saveFile.createNewFile()) {
					System.out.println("\nNew file successfully created."); //debug
				} else {
					System.out.println("\nNew file was not created."); //debug
				}
				
				tempPlayer.close();
				
				try {
					FileWriter savePlayer = new FileWriter(saveFile);
					savePlayer.write(getMaxLevel() + sp + getMaxHealth() + sp + getHealth() + sp + getAttack() + sp + getDefense()
					+ sp + getSpeed() + sp + getKeys() + sp + getMoney() + sp + getBombs());
					
					System.out.println("\nNew file saved successfully."); //debug
					
					if (tempFile.delete()) {
						System.out.println("\nTemporary file deleted successfully."); //debug
					} else {
						System.out.println("\nTemporary file was not deleted, somehow."); //debug
					}
					
					savePlayer.close();
					return true;
				
				} catch (Exception ex) {
					System.out.println(ex);
					return false;
				}
				
				
			} else {
				System.out.println("\nSomething has gone wrong, unable to save.\n In the event that your old file no longer works, try typing in \"tempPlayer\" after choosing \"Load\".");
			}
			
		} catch(Exception ex) {
			System.out.println(ex);
			return false;
		}

		return false;
	}

	
	//Typical getters and setters, all the way down the line
	

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

	public void setHealth(int health) {
		this.health = health;
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

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public int getKeys() {
		return keys;
	}

	public void setKeys(int keys) {
		this.keys = keys;
	}

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		this.money = money;
	}

	public int getBombs() {
		return bombs;
	}

	public void setBombs(int bombs) {
		this.bombs = bombs;
	}

	
	public String getPlayerFileName() {
		return playerFileName;
	}

	
	public void setPlayerFileName(String playerFileName) {
		this.playerFileName = playerFileName;
	}
	
	
//	try {
//		FileWriter savePlayer = new FileWriter(playerFileName + ".txt");
//		//Couldn't be bothered to type " " 14 times
//		String sp = " ";
//		
//		//Player file will start a new line and save all the current loaded data.
//		savePlayer.write("/n" + getMaxLevel() + sp + getMaxHealth() + sp + getHealth() + sp + getAttack() + sp + getDefense()
//							+ sp + getSpeed() + sp + getKeys() + sp + getMoney() + sp + getBombs());
//	}
}
