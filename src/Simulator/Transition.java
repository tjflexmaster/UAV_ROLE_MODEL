package Simulator;

import java.util.ArrayList;



/**
 * @author rob.ivie
 */
public class Transition {
	
	private ArrayList<UDO> _inputs;
	private Duration _duration;
	private State _endState;
	private ArrayList<UDO> _outputs;
	private int _priority;
	//private State _curState;


	/**
	 * a transition is used by an agent (state machine) to formally define state transitions 
	 * @param input the necessary input an agent needs before it can make the transition
	 * @param endState the new state the agent will move to
	 * @param output the output the transition produces
	 * @param curState the current state of the actor
	 * @param priority the priority level of the transition
	 * todo add a duration that represents how long it takes to move between states
	 */
	public Transition (ArrayList<UDO> inputs, ArrayList<UDO> outputs, State endState, Duration duration, int priority) {
		
		_inputs = inputs;
		_outputs = outputs;
		_endState = endState;
		//_curState = curState;
		_duration = duration;
		_priority = priority;
		
	}

//	/**
//	 * a transition is used by an agent (state machine) to formally define state transitions 
//	 * @param startState the state an agent must be in before it can make the transition
//	 * @param time the necessary input an agent needs before it can make the transition
//	 * @param nextState the new state the agent will move to
//	 * @param output the output the transition produces
//	 * todo add a duration that represents how long it takes to move between states
//	 */
//	public Transition(int time, State nextState, ArrayList<UDO> outputs) {
//		
//		
//		_time = time; 
//		_nextState = nextState;
//		_outputs = outputs;
//		
//	}
	
	public boolean isPossible(){
		for(UDO input : _inputs){
			if(!input.isActive()){
				return false;
			}
		}
		return true;
	}
	
	public State process(){
		for(UDO output : _outputs){
			output.setActive();
		}
		return _endState;
	}
	
	public Duration getDuration(){
		
		return _duration;
		
	}
	
	public int getPriority(){
		return _priority;
	}
	
	public String toString() {
		
		String result = "";
		
		//result += _curState + " X ";
		if ( _inputs == null ) {
			result += _duration.getMaximum();
		} else {
			result += _inputs.toString();
		}
		result += " -> " + _endState.toString() + " X " + _outputs.toString();
		
		return result;
		
	}
	
}
