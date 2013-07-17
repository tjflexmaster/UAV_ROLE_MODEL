package model;

import model.team.WiSARTeam;
import simulator.ITeam;
import simulator.Simulator;

public class WiSARModel {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Load a Simulator, Load a Team, start the simulator running.
		ITeam team = new WiSARTeam();
		
		Simulator sim = new Simulator(team, Simulator.Mode.DEBUG, Simulator.DurationMode.MIN);
		
		sim.run();

	}

}
