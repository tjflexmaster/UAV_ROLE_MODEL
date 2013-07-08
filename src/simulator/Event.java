package simulator;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class Event {
	protected String _name = "";
	private ArrayList<IState> _states;
	private ActorVariableWrapper _internal_vars = new ActorVariableWrapper();
	
	abstract protected void initializeInternalVariables();
	
	/**
	 * This method must be implemented by the Event.  
	 * @return
	 */
	abstract public ITransition getTransition();
}
