package simulator;

import java.util.Scanner;

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
		while (clock.tick() != -1) {
			System.out.println(clock.toString());
			getUserCommand();
		}
		
		//close the scanner once the simulation is complete
		scanner.close();
	}
	
	/**
	 * this method prints swim lanes and accepts user commands
	 * @param currentTime 
	 */
	private static void getUserCommand() {
		// TODO Auto-generated method stub
		
	}
	
}
