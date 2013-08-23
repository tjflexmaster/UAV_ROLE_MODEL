package model;

import model.team.WiSARTeam;
import simulator.*;
import simulator.Simulator.*;

public class WiSARModel {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Load a Simulator, Load a Team, start the simulator running.
		
		Simulator sim = Simulator.getSim();
		
		sim.setup(new WiSARTeam(), DebugMode.DEBUG, DurationMode.MIN);
		
		sim.run();
	}
}
