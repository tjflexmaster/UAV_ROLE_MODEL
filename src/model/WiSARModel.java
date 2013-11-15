package model;

import model.team.WiSARTeam;
import simulator.*;
import simulator.Simulator.*;

public class WiSARModel {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Simulator sim = Simulator.getSim();
		
		sim.setup(new WiSARTeam(), DebugMode.DEBUG, DurationMode.MIN_MAX);
		
		sim.run();
	}
}
