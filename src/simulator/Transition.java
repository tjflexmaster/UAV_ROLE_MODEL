package simulator;

import team.*;

/**
 * this class is a models all transitions in the simulation 
 * @author tjr team
 * 
 */
public class Transition {
	
	private UDO[] _inputs;
	public Duration _duration;
	private State _endState;
	private UDO[] _outputs;
	public int _priority;


	/**
	 * a transition is used by an agent (state machine) to formally define state transitions 
	 * @param input the necessary input an agent needs before it can make the transition
	 * @param endState the new state the agent will move to
	 * @param output the output the transition produces
	 * @param curState the current state of the actor
	 * @param priority the priority level of the transition
	 * todo add a duration that represents how long it takes to move between states
	 * @return 
	 */
	public Transition (UDO[] inputs, UDO[] outputs, State endState, Duration duration, int priority) {
		
		_inputs = inputs;
		_outputs = outputs;
		_endState = endState;
		_duration = duration;
		_priority = priority;
	}
	
	/**
	 * @return return whether the transition can be made based on the state of the UDOs
	 */
	public boolean isEnabled(){
		for(UDO input : _inputs){
			if(input.get().getClass()==Boolean.class){//handle boolean signals
				if(!(Boolean)input.get()){
					return false;
				}
			}else if(input.get().getClass()==Integer.class){//handle integer counters
				input.update((Integer)input.get()-(Integer)UDO.DC_TIME_ELAPSED.get());
				if((Integer)input.get()>0){
					return false;
				}
			}
		}
		deactivateInput();//the transition can happen, new forget the inputs
		return true;
	}

	public void deactivateInput() {
		for(UDO input : _inputs){
			input.set(null);
		}
	}
	
	/**
	 * 
	 * @return the new state of the actor after the transition is processes 
	 */
	public State fire(Boolean active){
		
		for(UDO output : _outputs){
			output.set(active);
		}
		return _endState;
	}
	
	/**
	 * 
	 * @return return a string representation of the transition
	 */
	public String toString() {
		return _inputs.toString() + " -> " + _endState.toString() + " X " + _outputs.toString();
	}
	
}
