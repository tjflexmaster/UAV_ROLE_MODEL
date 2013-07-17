package simulator;

import java.util.*;

/**
 * this abstract class is extended by the actors of the simulation
 * it contains the variables and methods that compose an actor
 * @author tjr team
 *
 */
public abstract class Actor implements IActor {
	/**
	 * this variable represents the name we give to the actor
	 */
	protected String _name = "";
	
	private ArrayList<IState> _states;
	
	/**
	 * this represents the current state of the actor (state machine)
	 */
	private ActorVariableWrapper _internal_vars = new ActorVariableWrapper();
	
	abstract protected void initializeInternalVariables();
	
	/**
	 * This method must be implemented by the Actor.  
	 * @return
	 */
	abstract public HashMap<IActor, ITransition> getTransitions();
	
	/**
	 * this represents all of the subactors that this actor holds
	 */
	private ArrayList<Actor> _subactors = null;
	
	
	/**
	 * 
	 * @return return the current state of the actor
	 */
	public State getCurrentState() {
		return (State) _internal_vars.getVariable("currentState");
	}
	
	public String name()
	{
		return _name;
	}
	
	protected void setInternalVariable(String name, Object value)
	{
		_internal_vars.setVariable(name, value);
	}
	
	protected ActorVariableWrapper getInternalVars()
	{
		return _internal_vars;
	}
	
	protected ArrayList<Actor> getSubActors()
	{
		return _subactors;
	}
	
	protected void addSubActor(Actor a)
	{
		_subactors.add(a);
	}

	
	
	/**
	 * HELPER METHODS
	 */
	protected void startState(State state)
	{
		assert _states.contains(state):"Start state not available.";
		_internal_vars.setVariable("currentState", state);
	}
	
	protected Actor add(IState state)
	{
		if ( _states.contains(state) )
			return this;
		
		_states.add(state);
		return this;
	}

	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Actor other = (Actor) obj;
		if (_name == null) {
			if (other.name() != null)
				return false;
		} else if (!_name.equals(other.name()))
			return false;
		return true;
	}
	

	@Override
	public int hashCode()
	{
		return _name.hashCode();
	}
	
	/**
	 * this method works like a normal toSTring method
	 * @return return the string representation of the actor
	 */
	public String toString() {
		String result = "";
		
		result += _name + "(" + "): " + ((State)_internal_vars.getVariable("currentState")).toString() + " X ";
		
		return result;
	}
}
