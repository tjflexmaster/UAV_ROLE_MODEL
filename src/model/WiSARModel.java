package model;

import model.team.WiSARTeam;
import simulator.DOM;
import simulator.ITeam;
import simulator.Simulator;

public class WiSARModel {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Load a Simulator, Load a Team, start the simulator running.
//		ITeam team = new WiSARTeam();
		DOM d = new DOM("C:\\Users\\TJ-ASUS\\git\\UAV_ROLE_MODEL\\src\\model\\team\\BasicWiSARTeam.xml");
		
		Simulator sim = new Simulator(d.getTeam(), Simulator.Mode.DEBUG, Simulator.DurationMode.MIN);
		
		sim.run();

	}

}
