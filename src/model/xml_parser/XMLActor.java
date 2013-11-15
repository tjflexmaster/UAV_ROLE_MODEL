package model.xml_parser;

import simulator.Actor;
import simulator.IActor;
import simulator.IState;

public class XMLActor extends Actor implements IActor {

	public XMLActor(String name)
	{
		setName(name);
	}
	
	@Override
	protected void initializeInternalVariables() {
		// TODO Auto-generated method stub

	}

	public void addState(IState state)
	{
		add(state);
	}
}
