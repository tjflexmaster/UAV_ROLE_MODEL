package WiSAR;

import CUAS.Simulator.Simulator;
import CUAS.Utils.DurationGenerator.Mode;

public class Application {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		Simulator simulator = new Simulator(Mode.MIN, WiSARDurations.durations(), new DefaultTeam());
//		Simulator simulator = new Simulator();
		try {
			Simulator.getInstance().setup(Mode.MIN, new DefaultTeam());
			//Load Duration Assumptions
			
			//Load a Scenario
			Scenario.scenario1();
//			Scenario.scenario3();
			
			//Run the simulation
			Simulator.getInstance().run();
		} catch (AssertionError e) {
			System.out.println(e.getMessage());
		}
		
		
	}
}
