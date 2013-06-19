package simulator;

import java.util.ArrayList;

/**
 * this class represents a the state of an actor (state machine)
 * @author tjr team
 * 
 */
public class State {
	
	/**
	 * this is the name of the state
	 */
	private String _name;
	private ArrayList<Transition> _transitions;
	
	/**
	 * this constructor is used for creating new states
	 * @param name
	 */
	public State(String name) {
		_name = name;
		_transitions = new ArrayList<Transition>();
	}
	
	/**
	 * this adds a transition to the system organizing the array list by highest priorities first
	 * @param new_transition
	 * @return returns if the addition was successful
	 */
	public State addTransition(Transition new_transition){
		if(_transitions.contains(new_transition)){
			return this;
		}
		
		for(int index = 0; index < _transitions.size(); index++){
			Transition temp = _transitions.get(index);
			if(temp.getPriority() < new_transition.getPriority()){
				_transitions.add(new_transition);
				return this;
			}
		}
		_transitions.add(new_transition);
		
		return this;
	}
	
	/**
	 * this method works like a normal toString method
	 * @return return the string representation of this state
	 */
	public String toString() {
		return _name;
	}

	/**
	 * this finds the first possible transition from the array list, most state classes will overide this method
	 * @return	the next possible transition with the highest priority, null if none are possible
	 */
	public Transition getNextTransition(){
		for(Transition transition : _transitions){
			if(transition.isPossible()){
				return transition;
			}
		}
		
		return null;
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

}
