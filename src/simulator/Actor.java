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
	protected String _name = "";
	
	/**
	 * the simulator's delta clock uses this variable
	 * this variable represents the time until the actor makes the transition, which is saved as currentTransition
	 */
	protected int _nextTime = -1;
	
	/**
	 * this represents the current transition the actor (state machine) is making
	 * this can be preempted given the right inputs and current state 
	 */
	protected Transition _currentTransition = null;
	
	/**
	 * this is a formally defined list of states the actor (state machine) can make during the simulation
	 */
	protected ArrayList<State> _states = new ArrayList<State>();
	
	/**
	 * this represents the current state of the actor (state machine)
	 */
	protected State _currentState = null;
	
	/**
	 * this method tells the actor to look at its current state and inputs
	 * if appropriate a new transition will be scheduled
	 * @param currentTime 
	 * @return returns true if a new transition has been scheduled
	 */
	public boolean updateTransition(){
		if(_currentState == null){
			return false;
		}
		Transition nextTransition = _currentState.getNextTransition();
		if(nextTransition.equals(_currentTransition) || nextTransition._priority < _currentTransition._priority){
			return false;
		}
		_currentTransition = nextTransition;
		return true;
	}
	
	/**
	 * this method tells the actor to process its current transition if appropriate
	 * @return returns true if the current transition has been processed
	 */
	public boolean processTransition(){
		if(_nextTime == 0){
			setCurrentState(_currentTransition.fire(new Boolean(true)));
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
	 * builds a state for the actor
	 * @param name
	 */
	public void addState(String name){
		_states.add(new State(name));
	}

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
}
