package Utilities;

import java.util.ArrayList;

/**
 * 
 * @author rob.ivie
 * this class is still incomplete
 */
public abstract class State {
	
	/**
	 * this is the name of the state
	 */
	private String _name;
	
	/**
	 * this formally lists all of the transitions that can be made from this state
	 */
	private ArrayList<Transition> _transitions;
	
	/**
	 * this constructor is used for creating new states
	 * @param name
	 */
	public State(String name) {
		
		_name = name;
		
	}
	
	public void addTransition(Transition t){
		
		_transitions.add(t);
		
	}
	
	public String toString() {
		
		return _name;
		
	}
	

	/**
	 * 
	 * @param _inputs
	 * @return	the next possible transition with the highest priority, null if none are possible
	 */
	abstract Transition getNextTransition(ArrayList<UDO> _inputs);

}
