package model.xml_parser;

import simulator.IActor;
import simulator.ITeam;
import simulator.Team;

public class XMLTeam extends Team implements ITeam {
	
	public String _name;
	
	public XMLTeam(String name) {
		this._name = name;
	}
	
	@Override
	public void addActor(IActor actor) 
	{
		assert !_actors.contains(actor):"Actor is already a part of the team";
		
		_actors.add(actor);
	}
}
