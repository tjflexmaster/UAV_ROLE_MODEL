package simulator;

/**
 * this class is a models all transitions in the simulation 
 * @author tjr team
 * 
 */
public class Transition {
	
	private UDO[] _inputs;
	private Duration _duration;
	private State _endState;
	private UDO[] _outputs;
	private int _priority;


	/**
	 * a transition is used by an agent (state machine) to formally define state transitions 
	 * @param input the necessary input an agent needs before it can make the transition
	 * @param endState the new state the agent will move to
	 * @param output the output the transition produces
	 * @param curState the current state of the actor
	 * @param priority the priority level of the transition
	 * todo add a duration that represents how long it takes to move between states
	 */
	public Transition (UDO[] inputs, UDO[] outputs, State endState, Duration duration, int priority) {
		
		_inputs = inputs;
		_outputs = outputs;
		_endState = endState;
		_duration = duration;
		_priority = priority;
		
	}
	
	/**
	 * 
	 * @return return whether the transition can be made based on the state of the UDOs
	 */
	public boolean isPossible(){
		for(UDO input : _inputs){
			if(!input.isActive()){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 
	 * @return the new state of the actor after the transition is processes 
	 */
	public State makeTransition(){
		for(UDO output : _outputs){
			output.prime();
		}
		return _endState;
	}
	
	/**
	 * 
	 * @return returns the expected time until the transition is processed (a duration)
	 */
	public Duration getDuration(){
		return _duration;
	}
	
	/**
	 * 
	 * @return return the prioritized value of the transition
	 */
	public int getPriority(){
		return _priority;
	}
	
	/**
	 * 
	 * @return return a string representation of the transition
	 */
	public String toString() {
		String result = "";
		
		//result += _curState + " X ";
		if ( _inputs == null ) {
			result += _duration.toString();
		} else {
			result += _inputs.toString();
		}
		result += " -> " + _endState.toString() + " X " + _outputs.toString();
		
		return result;
	}
	
}
