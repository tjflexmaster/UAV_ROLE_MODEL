package simulator;

import java.util.*;

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
	private int _nextTime = -1;
	
	/**
	 * this is a formally defined list of states the actor (state machine) can make during the simulation
	 */
	protected ArrayList<State> _states = new ArrayList<State>();
	
	/**
	 * this represents the current state of the actor (state machine)
	 */
	protected State _currentState = null;
	
	/**
	 * this represents the current transition the actor (state machine) is making
	 * this can be preempted given the right inputs and current state 
	 */
	protected Transition _currentTransition = null;
	private Transition _lastTransition = null;
	
	/**
	 * this represents all of the subactors that this actor holds
	 */
	protected ArrayList<Actor> _subactors = null;
	
	/**
	 * this method tells the actor to look at its current state and inputs
	 * if appropriate a new transition will be scheduled
	 * @param currentTime 
	 * @return returns true if a new transition has been scheduled
	 */
	public boolean updateTransition() {
		boolean updated = false;
		
		Transition nextTransition;
		if(_currentState != null && (nextTransition = _currentState.getNextTransition()) != null) {
			if (_currentTransition == null || _currentTransition.get_priority() < nextTransition.get_priority()) {
				_currentTransition = nextTransition;
				set_nextTime(_currentTransition.getDuration());
				updated = true;
			}
		}
		if(_subactors != null){
			for(Actor actor : _subactors){
				updated = actor.updateTransition()||updated;
			}
		}
		
		return updated;
	}
	
	/**
	 * this method tells the actor to process its current transition if appropriate
	 * @return returns true if the current transition has been processed
	 */
	public boolean processTransition(){
		boolean processed = false;
		
		if(_currentTransition != null && get_nextTime() == 0){
			if(_lastTransition != null){
				_lastTransition.deactivateOutputs();
			}
			_currentState = _currentTransition.fire();
			_lastTransition = _currentTransition;
			_currentTransition = null;
			_nextTime = -1;
			processed = true;
		}
		if(_subactors != null){
			for(Actor actor : _subactors){
				processed = actor.processTransition()||processed;
			}
		}
		
		return processed;
	}

	public boolean isProcessed(){
		if(_currentTransition != null)
			return false;
		if(_subactors != null){
			for(Actor actor : _subactors){
				if(actor._currentTransition != null)
					return false;
			}
		}
		return true;
	}

	/**
	 * this method allows the simulator viewing access to this classes nextTime 
	 * @return the time until the next transition
	 */
	public int get_nextTime(){
		return _nextTime;
	}
	
	/**
	 * this method lets the simulator update the time to reflect a delta clock value
	 * @param _nextTime
	 */
	public void set_nextTime(int nextTime){
		this._nextTime = nextTime;
	}
	
	/**
	 * builds a state for the actor
	 * @param name
	 */
	public void addState(State state){
		_states.add(state);
	}
	
	/**
	 * this method works like a normal toSTring method
	 * @return return the string representation of the actor
	 */
	public String toString() {
		String result = "";
		
		result += _name + "(" + get_nextTime() + "): ";
		if(_currentState != null){
			result += _currentState.toString();
		}
		result += " X ";
		if(_currentTransition != null){
			result += _currentTransition.toString();
		}
		if(_subactors != null){
			for(Actor actor : _subactors){
				if(actor.get_nextTime() == 0){
					result += '\n' + actor.toString();
				}
			}
		}
		
		return result;
	}
}
