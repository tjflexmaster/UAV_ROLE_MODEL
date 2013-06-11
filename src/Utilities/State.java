package Utilities;

import java.util.ArrayList;

public abstract class State {
	
	/**
	 * @author rob.ivie
	 * this is the name of the state
	 */
	private String name;
	private ArrayList<Transition> transitions;
	/**
	 * @param name
	 * this constructor is used for creating new states
	 */
	public State(String _name) {
		name = _name;
	}
	
	public void addTransition(Transition t){
		transitions.add(t);
	}
	
	public String toString() {
		return name;
	}
	

	/**
	 * 
	 * @param _inputs
	 * @return	the next possible transition with the highest priority, null if none are possible
	 */
	abstract Transition getNextTransition(ArrayList<UDO> _inputs);

}
