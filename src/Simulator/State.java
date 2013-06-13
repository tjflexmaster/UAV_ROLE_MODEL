package Simulator;

import java.util.ArrayList;


/**
 * 
 * @author rob.ivie
 * this class is still incomplete
 */
public class State {
	
	/**
	 * this is the name of the state
	 */
	private String _name;
	private ArrayList<Transition> transitions;
	
	/**
	 * this constructor is used for creating new states
	 * @param name
	 */
	public State(String name) {
		
		_name = name;
		
	}
	
	/**
	 * This adds a transition to the system organizing the array list by highest priorities first
	 * @param new_transition
	 * @return returns if the addition was successful
	 */
	public boolean addTransition(Transition new_transition){
		if(transitions.contains(new_transition)){
			return false;
		}
		for(int index = 0; index < transitions.size(); index++){
			Transition temp = transitions.get(index);
			if(temp.getPriority() < new_transition.getPriority()){
				transitions.add(new_transition);
				return true;
			}
		}
		transitions.add(new_transition);
		return true;
	}
	
	
	public String toString() {
		
		return _name;
		
	}

	/**
	 * This finds the first possible transition from the array list, most state classes will overide this method
	 * @return	the next possible transition with the highest priority, null if none are possible
	 */
	public Transition getNextTransition(){
		for(Transition transition : transitions){
			if(transition.isPossible()){
				return transition;
			}
		}
		return null;
	}
	
	/**
	 * overided the equals method to return true if the names of the two states match
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
