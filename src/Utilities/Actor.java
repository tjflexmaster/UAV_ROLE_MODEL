package Utilities;

import java.util.ArrayList;

public abstract class Actor {
	
	/**
	 * this variable represents the name we give to the actor
	 */
	private String _name = "Actor";
	
	/**
	 * the simulator's delta clock uses this variable
	 * this variable represents the time until the actor makes the transition, which is saved as currentTransition
	 */
	private int _nextTime = -1;
	
	/**
	 * this represents the current state of the actor (state machine)
	 */
	private State _currentState;
	
	/**
	 * this represents the current transition the actor (state machine) is making
	 * this can be preempted given the right inputs and current state 
	 */
	private Transition _currentTransition;
	
	/**
	 * this is a formally defined list of states the actor (state machine) can make during the simulation
	 */
	private ArrayList<State> _states;
	
	/**
	 * this method tells the actor to look at its current state and inputs
	 * if appropriate a new transition will be scheduled
	 * @return returns true if a new transition has been scheduled
	 */
	public boolean updateTransition() {
		
		return false;
		
	}
	
	/**
	 * this method tells the actor to process its current transition if appropriate
	 * @return returns true if the current transition has been processed
	 */
	public boolean processTransition(){
		
		if(_nextTime == 0){
			_currentState = _currentTransition.process();
			return true;
		}
		
		return false;
		
	}

	/**
	 * this method allows the simulator viewing access to this classes nextTime 
	 * @return the time until the next transition
	 */
	public int getNextTime() {
		//should we delete this method and make nextTime public?
		return _nextTime;
		
	}

	/**
	 * this method lets the simulator update the time to reflect a delta clock value
	 * @param nextTime
	 */
	public void setNextTime(int nextTime) {
		//should we delete this method and make nextTime public?
		this._nextTime = nextTime;
		
	}
	
	/**
	 * this method returns the string representation of the actor
	 */
	public String toString() {
		
		String result = "";
		
		result += _name + "(" + _nextTime + "): ";
		if (_currentTransition != null) {
			result += _currentTransition.toString();
		}
		
		return result;
		
	}
	
	
	//constructor classes
	
	public void addState(String name){
		states.add(new State(name));
	}
	
	public void addTransition(String state, ArrayList<UDO> inputs, ArrayList<UDO> outputs, State endState, int duration, int priority){
		//find the state and add the transition to it
		for(int index = 0; index < states.size(); index++){
			State temp = states.get(index);
			if(temp.equals(state)){
				temp.addTransition(new Transition(inputs,outputs,endState,duration,priority));
				return;
			}
		}
		//if the state doesn't exist yet, create a new state and add the transition
		State new_state = new State(state);
		new_state.addTransition(new Transition(inputs,outputs,endState,duration,priority));
		states.add(new_state);
	}
}
