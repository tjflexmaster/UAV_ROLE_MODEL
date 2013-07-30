package simulator;

import java.util.ArrayList;


/**
 * this class represents a the state of an actor (state machine)
 * @author tjr team
 * 
 */
public class State implements IState {
	
	/**
	 * this is the name of the state
	 */
	private String _name;
	private ArrayList<ITransition> _transitions;
	private ArrayList<Assertion> _assertions;
	
	/**
	 * this constructor is used for creating new states
	 * @param name
	 */
	public State(String name) {
		_name = name;
		_transitions = new ArrayList<ITransition>();
		_assertions = new ArrayList<Assertion>();
	}
	
	public State add(ITransition new_transition)
	{
		if(_transitions.contains(new_transition)){
			return this;
		}
		
		//The Actor will decide how to handle the transition and how to sort them.
		_transitions.add(new_transition);
		
		return this;
	}
	
	@Override
	public ArrayList<ITransition> getEnabledTransitions() {
		for(Assertion assertion : _assertions){
			assertion.checkAssertion();
		}
		ArrayList<ITransition> enabled = new ArrayList<ITransition>();
		for (ITransition t : _transitions) {
			if ( t.isEnabled() ) {
				//Copy the transition if it is enabled
				enabled.add((ITransition) new Transition((Transition)t));
			}
		}
		return enabled;
	}
	
	/**
	 * override the equals method to return true if the names of the two states match
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		State other = (State) obj;
		if (_name == null) {
			if (other._name != null)
				return false;
		} else if (!_name.equals(other._name))
			return false;
		return true;
	}
	
	@Override
	public int hashCode()
	{
		return _name.hashCode();
	}
	
	/**
	 * this method works like a normal toString method
	 * @return return the string representation of this state
	 */
	public String toString() {
		return _name;
	}

	public void addAssertion(Assertion assertion) {
		_assertions.add(assertion);
	}

}
