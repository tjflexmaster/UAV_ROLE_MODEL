package model.events;

import java.util.HashMap;

import simulator.Actor;
import simulator.IActor;
import simulator.ITransition;

public class NewSearchEvent extends Actor {
	
	public enum DATA_PS
	{
		NEW_SEARCH,
		TARGET_DESCRIPTION
	}

	@Override
	protected void initializeInternalVariables() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public HashMap<IActor, ITransition> getTransitions() {
		// TODO Auto-generated method stub
		return null;
	}

}
