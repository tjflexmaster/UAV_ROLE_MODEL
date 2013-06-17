package simulator;

public class Simulator {
	
	/**
	 * this method initializes the simulator
	 * then this method makes the clock run the simulation
	 * @param args should be left blank, all input will be ignored
	 */
	public static void main(String[] args) {
		//initialize simulation variables
		Team actors = new Team();
		DeltaClock clock = new DeltaClock();
		
		//run the simulator until the clock is empty
		clock.run(actors);
		
	}
	
}
