package simulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import model.team.Team;

public class Simulator {
	public static boolean debug = true;
	/**
	 * this method initializes the simulator
	 * then this method makes the clock run the simulation
	 * @param args should be left blank, all input will be ignored
	 */
	public static void main(String[] args) {
		//initialize simulation variables
		WiSARTeam team = new WiSARTeam();
		IDeltaClock clock = new DeltaClock();
		Scanner scanner = new Scanner(System.in);
		int runTime = 0;
		
		//run the simulator until the clock is empty
		do {
			//update next planned transition of all actors
			ArrayList<ITransition> readyTransitions = clock.getReadyTransitions();
			
			for (int index = 0; index < readyTransitions.size(); index++) {
				if (readyTransi) {
					clock.insert(actors.get(index));
					//done = false;
				}
			}
			
			//advance time
			readyTransitions = clock.getReadyTransitions();
			
			//communicate with the user
			runTime = communicateWithUser(scanner, clock, readyTransitions, runTime);
			
			//fire all ready transitions
			for (int index = 0; index < readyTransitions.size(); index++) {
				readyTransitions.get(index).fire();
			}
		} while (!readyTransitions.isEmpty());
		
		//close the scanner once the simulation is complete
		scanner.close();
	}
	
	/**
	 * this method prints swim lanes and accepts user commands
	 * @param scanner 
	 * @param clock
	 * @param readyTransitions
	 * @return return the integer time value to advance to
	 */
	private static int communicateWithUser(Scanner scanner, IDeltaClock clock, ArrayList<ITransition> readyTransitions, int runTime) {
		if(readyTransitions.isEmpty()){
			if(clock.elapsedTime() == 0){
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
			} else {
				System.out.println("Thank you for using the simulator, goodbye.");
				return runTime;
			}
		} else {
			System.out.println("----Time: " + clock.elapsedTime() + "----");
			for(ITransition transition : readyTransitions){
				System.out.println(transition.toString());
			}
		}
		
		if (runTime <= clock.elapsedTime()) {
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
