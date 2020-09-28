import java.util.Scanner;

public class AdventureApp {

	public static void main(String[] args) {
		//Floor test = new Floor(1);
		
		Scanner sc = new Scanner(System.in);
		String selection = "";
		
		System.out.println("To start a new game, type \"Start\"");
		System.out.println("To play with a preset character, type \"Load\"");
		System.out.println("To exit the program, type \"Exit\"");
		
		validResponse();

	}
	
	public static void validResponse() {
		String selection = "";
		String str;
		//GameManager runningGame = new GameManager();
		@SuppressWarnings("resource")
		Scanner sc = new Scanner(System.in);
		Scanner strSc = new Scanner(System.in);
		//sc.useDelimiter(System.lineSeparator());
		
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
				}
			
			} else if (selection.equalsIgnoreCase("Load")){
				System.out.print("Please input player file name: ");
				str = strSc.nextLine();
			
				//Allows input of temporary file names, for now, in case they are needed.
				Player playGame = new Player(str);
				if (playGame.savePlayer()) {
					GameManager runningGame = new GameManager(playGame);
				}
		
			} else if (selection.equalsIgnoreCase("Exit")) {
				System.out.println("Goodbye, have a nice day.");
				return;
			} else {
				System.out.println("Please enter a valid response. They are not case sensitive.");
			}
			
			selection = "";
			str = "";
			
			System.out.print("Selection: "); //Having issues here
				try {
					selection = sc.nextLine();
				} catch (Exception ex) {
					System.out.println(ex);
				}
			
		}
		
	}

}
