package simulator;

import java.util.ArrayList;

/**
 * this abstract class is extended by the actors of the simulation
 * it contains the variables and methods that compose an actor
 * @author tjr team
 *
 */
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
	 * this represents the current transition the actor (state machine) is making
	 * this can be preempted given the right inputs and current state 
	 */
	private Transition _currentTransition;
	
	/**
	 * this is a formally defined list of states the actor (state machine) can make during the simulation
	 */
	protected ArrayList<State> _states;
	
	/**
	 * this represents the current state of the actor (state machine)
	 */
	private State _currentState;
	
	/**
	 * this method tells the actor to look at its current state and inputs
	 * if appropriate a new transition will be scheduled
	 * @param currentTime 
	 * @return returns true if a new transition has been scheduled
	 */
	public abstract boolean updateTransition(int currentTime);
	
	/**
	 * this method tells the actor to process its current transition if appropriate
	 * @return returns true if the current transition has been processed
	 */
	public boolean processTransition(){
		if(_nextTime == 0){
			setCurrentState(_currentTransition.makeTransition());
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
	 * this method works like a normal toSTring method
	 * @return return the string representation of the actor
	 */
	public String toString() {
		String result = "";
		
		result += _name + "(" + _nextTime + "): ";
		if (_currentTransition != null) {
			result += _currentTransition.toString();
		}
		
		return result;
	}
	
	/**
	 * builds a state for the actor
	 * @param name
	 */
	public void addState(String name){
		_states.add(new State(name));
	}
	
	/*
	 * builds a transition for the actor
	 * @param state
	 * @param inputs
	 * @param outputs
	 * @param endState
	 * @param duration
	 * @param priority
	 *
	public void addTransition(String state, ArrayList<UDO> inputs, ArrayList<UDO> outputs, State endState, Duration duration, int priority){
		//find the state and add the transition to it
		for(int index = 0; index < _states.size(); index++){
			State temp = _states.get(index);
			if(temp.equals(state)){
				temp.addTransition(new Transition(inputs,outputs,endState,duration,priority));
				return;
			}
		}
		//if the state doesn't exist yet, create a new state and add the transition
		State new_state = new State(state);
		new_state.addTransition(new Transition(inputs,outputs,endState,duration,priority));
		_states.add(new_state);
	}*/

	/**
	 * 
	 * @return return the current state of the actor
	 */
	public State getCurrentState() {
		return _currentState;
	}

	public void setCurrentState(State _currentState) {
		this._currentState = _currentState;
	}
}
