package model;

import model.team.WiSARTeam;
import model.xml_parser.XmlModelParser;
import simulator.*;
import simulator.Simulator.*;

public class WiSARModel {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Simulator sim = Simulator.getSim();
		
		XmlModelParser xml = new XmlModelParser("src/test/basic_model.xml");
		
		sim.setup(xml.getTeam(), DebugMode.DEBUG, DurationMode.MIN);
		
		sim.run();
	}
}
