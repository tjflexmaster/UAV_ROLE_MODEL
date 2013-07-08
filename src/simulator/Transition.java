package simulator;

import java.util.HashMap;

import model.team.Duration;

/**
 * this class is a models all transitions in the simulation 
 * @author tjr team
 * 
 */
public class Transition implements ITransition {
	
	protected ComChannelList _inputs;
	private Duration _duration;
	private State _endState;
	private ComChannelList _outputs;
	private HashMap<String, Object> _temp_outputs;
	private int _priority;
	private double _probability;
	protected ActorVariableWrapper _internal_vars;
	private HashMap<String, Object> _temp_internal_vars;


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
	public Transition (ActorVariableWrapper internalVars, ComChannelList inputs, ComChannelList outputs, State endState, Duration duration, int priority, double probability) {
		
		_inputs = inputs;
		_outputs = outputs;
		_endState = endState;
		duration(duration);
		priority(priority);
		probability(probability);
		
		_internal_vars = internalVars;
		
		buildTempInternalVars();
		buildTempOutput();
	}

	public Transition (ActorVariableWrapper internalVars, ComChannelList inputs, ComChannelList outputs, State endState, Duration duration, int priority) {
		
		_inputs = inputs;
		_outputs = outputs;
		_endState = endState;
		duration(duration);
		priority(priority);
		probability(1);
		
		_internal_vars = internalVars;
		
		buildTempInternalVars();
		buildTempOutput();
	}
	
	public Transition (ActorVariableWrapper internalVars, ComChannelList inputs, ComChannelList outputs, State endState, Duration duration, double probability) {
		
		_inputs = inputs;
		_outputs = outputs;
		_endState = endState;
		duration(duration);
		priority(1);
		probability(probability);
		
		_internal_vars = internalVars;
		
		buildTempInternalVars();
		buildTempOutput();
	}
	
	public Transition (ActorVariableWrapper internalVars, ComChannelList inputs, ComChannelList outputs, State endState, Duration duration) {
		
		_internal_vars = internalVars;
		_inputs = inputs;
		_outputs = outputs;
		_endState = endState;
		duration(duration);
		priority(1);
		probability(1);
		
		buildTempInternalVars();
		buildTempOutput();
	}
	
	public Transition (ActorVariableWrapper internalVars, ComChannelList inputs, ComChannelList outputs, State endState) {
	
		_internal_vars = internalVars;
		_inputs = inputs;
		_outputs = outputs;
		_endState = endState;
		duration(Duration.NEXT);
		priority(1);
		probability(1);
		
		buildTempInternalVars();
		buildTempOutput();
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
		
		buildTempInternalVars();
		buildTempOutput();
	}
	
	/**
	 * @return return whether the transition can be made based on the state of the ComChannels
	 */
	public boolean isEnabled() {
		return true;
	}
	
	/**
	 * 
	 * @return the new state of the actor after the transition is processes 
	 */
	@SuppressWarnings("rawtypes")
	public void fire(){
		
		if(!_temp_outputs.isEmpty()){
			for(ComChannel<?> output : _outputs.values()){
				Object temp = _temp_outputs.get(output.name());
				if ( temp != null )
					output.set(temp);
			}
		}
		
		if ( !_temp_internal_vars.isEmpty() ) {
			for(String var : _temp_internal_vars.keySet()) {
				Object temp = _temp_internal_vars.get(var);
				if ( temp != null )
					_internal_vars.setVariable(var, temp);
			}
		}
		
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
	 * HELPER METHODS
	 */
	private void buildTempOutput()
	{
		_temp_outputs = new HashMap<String, Object>();
		for( ComChannel<?> c : _outputs.values()) {
			_temp_outputs.put(c.name(), null);
		}
	}
	
	private void buildTempInternalVars()
	{
		_temp_internal_vars = new HashMap<String, Object>();
		HashMap<String, Object> vars = _internal_vars.getAllVariables();
		for(String s : vars.keySet()) {
			_temp_internal_vars.put(s, null);
		}
	}
	
	protected void setTempOutput(String varname, Object value)
	{
		assert _temp_outputs.containsKey(varname): "Cannot set temp output, variable does not exist";
		_temp_outputs.put(varname, value);
	}
	
	protected void setTempInternalVar(String varname, Object value)
	{
		assert _temp_outputs.containsKey(varname): "Cannot set temp internal var, variable does not exist";
		_temp_internal_vars.put(varname, value);
	}

	@Override
	public int getDuration() {
		return _duration.getdur();
	}
	
	
//	/**
//	 * 
//	 * @return return a string representation of the transition
//	 */
//	public String toString() {
//		String result = "(";
//		if(_inputs != null){
//			for(ComChannel input : _inputs) {
//				result += input.name() + " ";
//			}
//		}
//		result += ") -> " + _endState.toString() + " X (";
//		if(_outputs != null){
//			for(ComChannel output : _outputs) {
//				result += output.name() + " ";
//			}
//		}
//		result += ")";
//		return result;
//	}

}
