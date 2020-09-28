import java.util.Scanner;

public class GameManager {
	private int level = 0;
	private Player player;
	
	
	public GameManager(Player player){
		setPlayer(player);
		System.out.println("Player loaded into game successfully.");
	}
	
	
	public void debug( ) {
		Scanner stop = new Scanner(System.in);
		Floor currentFloor = new Floor(level);
		currentFloor.visualize();
		String stopIt = stop.nextLine();
		System.out.println("\nTest over.");
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
	
	
}
