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
		ArrayList<Actor> readyActors = new ArrayList<Actor>();
		int runTime = 0;
		
		//run the simulator until the clock is empty
		do {
			//update next planned transition of all actors
			for (int index = 0; index < actors.size(); index++) {
				if (actors.get(index).updateTransition()) {
					clock.insert(actors.get(index));
					//done = false;
				}
			}
			
			//advance time
			readyActors = clock.tick();
			
			//communicate with the user
			runTime = communicateWithUser(scanner, clock, readyActors, runTime);
			
			//fire all ready transitions
			for (int index = 0; index < readyActors.size(); index++) {
				readyActors.get(index).processTransition();
			}
		} while (!readyActors.isEmpty());
		
		//close the scanner once the simulation is complete
		scanner.close();
	}
	
	/**
	 * this method prints swim lanes and accepts user commands
	 * @param scanner 
	 * @param clock
	 * @param readyActors
	 * @return return the integer time value to advance to
	 */
	private static int communicateWithUser(Scanner scanner, DeltaClock clock, ArrayList<Actor> readyActors, int runTime) {
		if(readyActors.isEmpty()){
			System.out.println("Would you like to run in debug mode (y / n)?");
			String response = scanner.nextLine();
			if(response.equalsIgnoreCase("y")){
				debug = true;
				System.out.println("Entering Debug Mode");
			}else if(response.equalsIgnoreCase("n")){
				debug = false;
				System.out.println("Running Simulation");
			}else{
				debug = true;
				System.out.println("Entering Debug Mode");
			}
		}
		
		for(Actor actor : readyActors){
			if(debug && actor.get_nextTime() == 0){
				System.out.println("----Time at: " + clock.getAbsoluteTime() + "----");
				System.out.println(actor.toString());
			}
		}
		
		if (runTime <= clock.getAbsoluteTime()) {
			System.out.println("In integer format enter a time to skip to. Then press Enter.");
			String response = scanner.nextLine();
			try {
				runTime = Integer.parseInt(response);
			} catch(NumberFormatException e) {
				System.out.println("Contining to the next transition.");
			}
		}
		return runTime;
	}
	
}
