package simulator;

import model.team.Duration;
import model.team.UDO;

/**
 * this class is a models all transitions in the simulation 
 * @author tjr team
 * 
 */
public class Transition {
	
	protected UDO[] _inputs;
	private Duration _duration;
	private State _endState;
	private UDO[] _outputs;
	private int _priority;
	private double _probability;
	private ActorVariableWrapper _internal_vars;


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
	public Transition (ActorVariableWrapper internalVars, UDO[] inputs, UDO[] outputs, State endState, Duration duration, int priority, double probability) {
		
		_inputs = inputs;
		_outputs = outputs;
		_endState = endState;
		duration(duration);
		priority(priority);
		probability(probability);
		
		_internal_vars = internalVars;
	}

	public Transition (ActorVariableWrapper internalVars, UDO[] inputs, UDO[] outputs, State endState, Duration duration, int priority) {
		
		_inputs = inputs;
		_outputs = outputs;
		_endState = endState;
		duration(duration);
		priority(priority);
		probability(1);
		
		_internal_vars = internalVars;
	}
	
	public Transition (ActorVariableWrapper internalVars, UDO[] inputs, UDO[] outputs, State endState, Duration duration, double probability) {
		
		_inputs = inputs;
		_outputs = outputs;
		_endState = endState;
		duration(duration);
		priority(1);
		probability(probability);
		
		_internal_vars = internalVars;
	}
	
	public Transition (ActorVariableWrapper internalVars, UDO[] inputs, UDO[] outputs, State endState, Duration duration) {
		
		_internal_vars = internalVars;
		_inputs = inputs;
		_outputs = outputs;
		_endState = endState;
		duration(duration);
		priority(1);
		probability(1);
		
		
	}
	
	public Transition (ActorVariableWrapper internalVars, UDO[] inputs, UDO[] outputs, State endState) {
	
		_internal_vars = internalVars;
		_inputs = inputs;
		_outputs = outputs;
		_endState = endState;
		duration(Duration.NEXT);
		priority(1);
		probability(1);
		
		
	}
	
	public Transition(Transition t)
	{
		_internal_vars = t._internal_vars;
		_endState = t._endState;
		_inputs = t._inputs;
		_outputs = t._outputs;
		_duration = t._duration;
		_priority = t._priority;
		_probability = t._probability;
		
	}
	
	/**
	 * @return return whether the transition can be made based on the state of the UDOs
	 */
	public boolean isEnabled() {
		return true;
	}
	
	/**
	 * 
	 * @return the new state of the actor after the transition is processes 
	 */
	public void fire(Boolean active){
		
		/**
		 * REDO THIS SECTION
		 * *************************
		 */
		if(_outputs != null){
			for(UDO output : _outputs){
				output.set(active);
			}
		}
		/**
		 * *************************
		 */
		
		//Update the actor state
		_internal_vars.setVariable("currentState", _endState);
	}
	
	/**
	 * Getters
	 */
	public int duration()
	{
		return _duration.getdur();
	}
	
	public void duration(Duration d)
	{
		_duration = d;
	}
	
	public int priority()
	{
		return _priority;
	}
	
	public void priority(int p)
	{
		p = Math.max(0, p);
		
		_priority = p;
	}
	
	public void probability(double p)
	{
		p = Math.min(1, p);
		p = Math.max(0, p);
		
		_probability = p;
	}
	
	public double probability()
	{
		return _probability;
	}
	
	/**
	 * 
	 * @return return a string representation of the transition
	 */
	public String toString() {
		String result = "(";
		if(_inputs != null){
			for(UDO input : _inputs) {
				result += input.name() + " ";
			}
		}
		result += ") -> " + _endState.toString() + " X (";
		if(_outputs != null){
			for(UDO output : _outputs) {
				result += output.name() + " ";
			}
		}
		result += ")";
		return result;
	}

}
