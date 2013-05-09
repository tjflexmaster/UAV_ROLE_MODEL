package WiSAR;

import CUAS.Simulator.Simulator;
import CUAS.Utils.DurationGenerator.Mode;
import WiSAR.Events.NewSearchAOIEvent;
import WiSAR.Events.SearchTargetDescriptionEvent;
import WiSAR.Events.TargetSightingFalseEvent;
import WiSAR.Events.TargetSightingTrueEvent;

public class Application {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		Simulator simulator = new Simulator(Mode.MIN, WiSARDurations.durations(), new DefaultTeam());
//		Simulator simulator = new Simulator();
		try {
			Simulator.getInstance().setEnvironment(Simulator.Environment.DEBUG);
			Simulator.getInstance().setup(Mode.MIN, new DefaultTeam());
			//Load Duration Assumptions
			
			
			Simulator.getInstance().addEvent(new NewSearchAOIEvent(1));
			Simulator.getInstance().addEvent(new SearchTargetDescriptionEvent(1));
			Simulator.getInstance().addEvent(new TargetSightingTrueEvent(1));
//			Simulator.getInstance().addEvent(new TargetSightingFalseEvent(3));
			//Load a Scenario
//			Scenario.scenario1();
//			Scenario.scenario3();
			
			//Run the simulation
			Simulator.getInstance().run();
		} catch (AssertionError e) {
			System.out.println(e.getMessage());
		}
		
		
	}
}
