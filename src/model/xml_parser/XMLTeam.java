package model.xml_parser;


import simulator.ITeam;
import simulator.Team;

public class XMLTeam extends Team implements ITeam {
	
	public String _name;
	
	public XMLTeam(String name) {
		this._name = name;
	}
	
}
