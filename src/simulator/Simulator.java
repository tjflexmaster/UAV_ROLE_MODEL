package simulator;

import java.util.*;

import team.*;

public class Simulator {
	public static boolean debug = true;
	/**
	 * this method initializes the simulator
	 * then this method makes the clock run the simulation
	 * @param args should be left blank, all input will be ignored
	 */
	public static void main(String[] args) {
		//initialize simulation variables
		Team actors = new Team();
		DeltaClock clock = new DeltaClock();
		Scanner scanner = new Scanner(System.in);
		boolean done = false;
		//run the simulator until the clock is empty
		do {
			done = true;
			//update next planned transition of all actors
			for (int index = 0; index < actors.size(); index++) {
				if (actors.get(index).updateTransition()) {
					clock.insert(actors.get(index));
					done = false;
				}
			}
			
			//fire all ready transitions
			ArrayList<Actor> readyActors = clock.tick();
			if(debug)
				System.out.println("Time at: " + clock.getAbsoluteTime());
			for (int index = 0; index < readyActors.size(); index++) {
				if(readyActors.get(index).processTransition())
					done = false;
			}
			done = readyActors.isEmpty();
		} while (!done);
		
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
