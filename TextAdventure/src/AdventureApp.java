import java.util.Scanner;

/**
 * @author Jordan S
 * Loaded on startup. Sets everything up to play the game.
 * Calls the Game Manager
 */

public class AdventureApp {

	public static void main(String[] args) { //Starts up with a greeting and goes straight into the response loop
		
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		System.out.println("  Welcome to the endless dungeon!  ");
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		
		System.out.println("/nTo start a new game, type \"Start\"");
		System.out.println("To play with a preset character, type \"Load\"");
		System.out.println("To exit the program, type \"Exit\"");
		
		validResponse();

	}
	
	public static void validResponse() { //Users can create/load a file or leave
		String selection = "";
		String str;
		@SuppressWarnings("resource")
		Scanner sc = new Scanner(System.in);
		@SuppressWarnings("resource")
		Scanner strSc = new Scanner(System.in);
		
		//Basic selection, not case sensitive
		
		System.out.print("Selection: ");
		selection = sc.nextLine();
		
		while (!selection.equalsIgnoreCase("exit")) {
		
			if (selection.equalsIgnoreCase("Start")) {
			
				System.out.print("Enter the name of your new character: ");
				str = strSc.nextLine();
			
				//These file names are used in the event that a save gets interrupted and therefore cannot be used by the player.
				while (str.equals("tempPlayer") || str.equals("emergencyTempPlayer")) {
					System.out.print("\nPlease rename your file, that name is designated for temporary files: ");
					str = strSc.nextLine();
				}
			
				Player playGame = new Player(str);
				//Ensures the player file has loaded, written to the player class, and saved, before being sure to launch the game
				if (playGame.savePlayer()) {
					GameManager runningGame = new GameManager(playGame);
					runningGame.playGame();
				}
			
			} else if (selection.equalsIgnoreCase("Load")){
				System.out.print("Please input player file name: ");
				str = strSc.nextLine();
			
				//Allows input of temporary file names, for now, in case they are needed.
				Player playGame = new Player(str);
				if (playGame.savePlayer()) {
					GameManager runningGame = new GameManager(playGame);
					runningGame.playGame();
				}
		
			} else if (selection.equalsIgnoreCase("Exit")) {
				//Never seen, but it's the thought that counts
				System.out.println("Goodbye, have a nice day.");
				return;
			} else {
				System.out.println("Please enter a valid response. They are not case sensitive.");
			}
			
			selection = "";
			str = "";
			
			System.out.print("Selection: ");
			
					selection = sc.nextLine();
				
			
		}
		
	}

}
