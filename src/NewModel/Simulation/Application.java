package NewModel.Simulation;

import NewModel.Utils.DurationGenerator.Mode;
import WiSAR.WiSARDurations;
import NewModel.Simulation.Simulator;;

public class Application {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		Simulator simulator = new Simulator(Mode.MIN, WiSARDurations.durations(), new DefaultTeam());
//		Simulator simulator = new Simulator();
		try {
			Simulator.getInstance().setup(Mode.RANDOM, new WiSARDurations().durations(), new DefaultTeam());
			//Load Duration Assumptions
			
			//Load a Scenario
			Scenario.scenario1();
			
			//Run the simulation
			Simulator.getInstance().run();
		} catch (AssertionError e) {
			System.out.println(e.getMessage());
		}
		
		
	}

}
