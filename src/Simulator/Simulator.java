package Simulator;

import java.util.Scanner;

import Utilities.*;

public class Simulator {
	
	/**
	 * this method runs the simulator
	 * @param args should be left blank, all input will be ignored
	 */
	public static void main(String[] args) {
		//initialize simulation variables
		UDOList outputs = new UDOList();
		ActorsList actors = new ActorsList(outputs);
		DeltaClock clock = new DeltaClock();
		Scanner scanner = new Scanner(System.in);
		
		//run the simulator until the clock is empty
		clock.run(actors, scanner);
		
		//close variables
		scanner.close();
	}
	
}
