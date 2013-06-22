package simulator;

import java.util.*;

import team.*;

public class Simulator {
	
	/**
	 * this method initializes the simulator
	 * then this method makes the clock run the simulation
	 * @param args should be left blank, all input will be ignored
	 */
	public static void main(String[] args) {
		//initialize simulation variables
		Team actors = new Team();
		DeltaClock clock = new DeltaClock(actors);
		Scanner scanner = new Scanner(System.in);
		
		//run the simulator until the clock is empty
		while (clock.tick() != null) {
			//update next planned transition of all actors
			for (int index = 0; index < actors.size(); index++) {
				if (actors.get(index).updateTransition()) {
					clock.insert(actors.get(index));
				}
			}
			
			//fire all ready transitions
			ArrayList<Actor> readyActors = clock.tick();
			for (int index = 0; index < actors.size(); index++) {
				readyActors.get(index).processTransition();
			}
		}
		
		//close the scanner once the simulation is complete
		scanner.close();
	}
	
	/**
	 * this method prints swim lanes and accepts user commands
	 * @param scanner 
	 * @param currentTime 
	 */
	private static String getUserCommand(Scanner scanner) {
		System.out.println("---------------------------------------------");
		System.out.println("Press Enter to advance to the next clock tick");
		String userCommand = scanner.nextLine();
		return userCommand;
	}
	
}
