package NewModel.Simulation;

public class Application {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Simulator simulator = new Simulator();
		
		//Load a Scenario
		Scenario.scenario1();
		
		simulator.run();
		
		
	}

}
