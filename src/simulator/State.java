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
<<<<<<< HEAD
	private ArrayList<Assertion> _assertions;
	
=======
	private int _workload;
>>>>>>> refs/heads/workload
	/**
	 * this constructor is used for creating new states
	 * @param name
	 */
	public State(String name, int workload) {
		_name = name;
		_transitions = new ArrayList<ITransition>();
<<<<<<< HEAD
		_assertions = new ArrayList<Assertion>();
=======
		_workload = workload*2;
>>>>>>> refs/heads/workload
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
			if ( t.updateTransition() ) {
				//Copy the transition if it is enabled
				if(t instanceof Transition)
					enabled.add((ITransition) new Transition((Transition)t));
				else if(t instanceof ActivateEventTransition)
					enabled.add((ITransition) new ActivateEventTransition((ActivateEventTransition)t));
				else if(t instanceof DeactivateEventTransition)
					enabled.add((ITransition) new DeactivateEventTransition((DeactivateEventTransition)t));
			}
		}
		return enabled;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_name == null) ? 0 : _name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
<<<<<<< HEAD
//		if (getClass() != obj.getClass())
//			return false;
		if(obj.getClass().equals(String.class) && !_name.equals((String)obj)){
=======
		if(obj instanceof String && _name.equals(obj))
			return true;
		if (!(obj instanceof State))
>>>>>>> refs/heads/workload
			return false;
		}
		if(obj.getClass().equals(State.class)){
			State other = (State) obj;
			if (_name == null) {
				if (other._name != null)
					return false;
			} else if (!_name.equals(other._name))
				return false;
		}
		return true;
	}
<<<<<<< HEAD
	
	@Override
	public int hashCode()
	{
		return _name.hashCode();
	}
	
	public String getName() {
		return _name;
	}
	
=======

>>>>>>> refs/heads/workload
	/**
	 * this method works like a normal toString method
	 * @return return the string representation of this state
	 */
	public String toString() {
		return _name;
	}

<<<<<<< HEAD
	public void addAssertion(Assertion assertion) {
		_assertions.add(assertion);
=======
	public int getWorkload() {
		int temp_workload = _workload + ((Transition)_transitions.get(0)).getWorkload();
		return temp_workload;
>>>>>>> refs/heads/workload
	}

}
