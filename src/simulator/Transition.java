package simulator;

import java.util.HashMap;
import java.util.Map.Entry;

/**
 * this class is a models all transitions in the simulation 
 * @author tjr team
 * 
 */
public class Transition implements ITransition {
	
	protected ComChannelList _inputs;
	private Range _range;
	private State _endState;
	protected ComChannelList _outputs;
	private HashMap<String, Object> _temp_outputs;
	private int _priority;
	private double _probability;
	protected ActorVariableWrapper _internal_vars;
	private HashMap<String, Object> _temp_internal_vars;
	private int _transition_number;


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
	public Transition (ActorVariableWrapper internalVars, ComChannelList inputs, ComChannelList outputs, State endState, Range duration_range, int priority, double probability) {
		
		_inputs = new ComChannelList();
		for(Entry<String, ComChannel<?>> chan : inputs.entrySet()){
			_inputs.add(chan.getValue());
		}
		_outputs = new ComChannelList();
		for(Entry<String, ComChannel<?>> chan : outputs.entrySet()){
			_outputs.add(chan.getValue());
		}
		_endState = endState;
		setDurationRange(duration_range);
		priority(priority);
		probability(probability);
		
		_internal_vars = internalVars;
		
		buildTempInternalVars();
		buildTempOutput();
	}

	public Transition (ActorVariableWrapper internalVars, ComChannelList inputs, ComChannelList outputs, State endState, Range duration_range, int priority) {

		_inputs = new ComChannelList();
		for(Entry<String, ComChannel<?>> chan : inputs.entrySet()){
			_inputs.add(chan.getValue());
		}
		_outputs = new ComChannelList();
		for(Entry<String, ComChannel<?>> chan : outputs.entrySet()){
			_outputs.add(chan.getValue());
		}
		_endState = endState;
		setDurationRange(duration_range);
		priority(priority);
		probability(1);
		
		_internal_vars = internalVars;
		
		buildTempInternalVars();
		buildTempOutput();
	}
	
	public Transition (ActorVariableWrapper internalVars, ComChannelList inputs, ComChannelList outputs, State endState, Range duration_range, double probability) {

		_inputs = new ComChannelList();
		for(Entry<String, ComChannel<?>> chan : inputs.entrySet()){
			_inputs.add(chan.getValue());
		}
		_outputs = new ComChannelList();
		for(Entry<String, ComChannel<?>> chan : outputs.entrySet()){
			_outputs.add(chan.getValue());
		}
		_endState = endState;
		setDurationRange(duration_range);
		priority(1);
		probability(probability);
		
		_internal_vars = internalVars;
		
		buildTempInternalVars();
		buildTempOutput();
	}
	
	public Transition (ActorVariableWrapper internalVars, ComChannelList inputs, ComChannelList outputs, State endState, Range duration_range) {
		
		_internal_vars = internalVars;

		_inputs = new ComChannelList();
		for(Entry<String, ComChannel<?>> chan : inputs.entrySet()){
			_inputs.add(chan.getValue());
		}
		_outputs = new ComChannelList();
		for(Entry<String, ComChannel<?>> chan : outputs.entrySet()){
			_outputs.add(chan.getValue());
		}
		_endState = endState;
		setDurationRange(duration_range);
		priority(1);
		probability(1);
		
		buildTempInternalVars();
		buildTempOutput();
	}
	
	public Transition (ActorVariableWrapper internalVars, ComChannelList inputs, ComChannelList outputs, State endState) {
	
		_internal_vars = internalVars;

		_inputs = new ComChannelList();
		for(Entry<String, ComChannel<?>> chan : inputs.entrySet()){
			_inputs.add(chan.getValue());
		}
		_outputs = new ComChannelList();
		for(Entry<String, ComChannel<?>> chan : outputs.entrySet()){
			_outputs.add(chan.getValue());
		}
		_endState = endState;
		_range = new Range();
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
		_range = t.getDurationRange();
		_priority = t._priority;
		_probability = t._probability;

		buildTempInternalVars();
		buildTempOutput();
		
		_temp_outputs.putAll(t._temp_outputs);
		for( Entry<String, Object> temp_internal_var : t._temp_internal_vars.entrySet()){
			if(temp_internal_var.getValue() != null)
				_temp_internal_vars.put(temp_internal_var.getKey(), temp_internal_var.getValue());
		}
	}
	
	public void clearTempData(){
		_temp_outputs.clear();
		_temp_internal_vars.clear();
	}
	
	public boolean updateTransition(){
//		for(Entry<String, Object> internal : _internal_vars.getAllVariables().entrySet()){
//			this.setTempInternalVar(internal.getKey(), internal.getValue());
//		}
		for(Entry<String, Object> internal : _temp_internal_vars.entrySet()){
			if(internal.getValue() != null){
				_temp_internal_vars.put(internal.getKey(), null);
			}
		}
		return isEnabled();
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
//		if(!_internal_vars.getVariable("currentState").equals(_endState)){
//			Simulator.getSim().addMetric((String)_internal_vars.getVariable("name"), "state_change", 1);
//		}
		if(!_temp_outputs.isEmpty()){
			for(ComChannel<?> output : _outputs.values()){
				Object temp = _temp_outputs.get(output.name());
				//if ( temp != null ){
					output.set(temp);
					_temp_outputs.put(output.name(), null);
				//}
			}
		}
		
		if ( !_temp_internal_vars.isEmpty() ) {
			for(String var : _temp_internal_vars.keySet()) {
				Object temp = _temp_internal_vars.get(var);
				if ( temp != null ){
					_internal_vars.setVariable(var, temp);
					_temp_internal_vars.put(var, null);
				}
			}
		}
		
		//Update the actor state
		_internal_vars.setVariable("currentState", _endState);
	}
	
	/**
	 * Getters
	 */
	@Override
	public Range getDurationRange() {
		return _range;
	}
	
	public void setDurationRange(Range range)
	{
		_range = range;
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
//		Simulator.getSim().addMetric((String)_internal_vars.getVariable("name"), _transition_number, MetricEnum.CHANNEL_TEMP_A);
//		Simulator.getSim().addMetric(MetricEnum.CHANNEL_TEMP_O, varname);
		_temp_outputs.put(varname, value);
	}
	
	protected void setTempInternalVar(String varname, Object value)
	{
		assert _temp_internal_vars.containsKey(varname): "Cannot set temp internal var, variable does not exist";
//		Simulator.Sim().addMetric(_internal_vars._variables.get("name").toString(), varname + "_temp_internal_set", 1);
//		Simulator.getSim().addMetric(MetricEnum.MEMORY_TEMP, varname);
		_temp_internal_vars.put(varname, value);
	}

	
	/**
	 * 
	 * @return return a string representation of the transition
	 */
	public String toString() {
		
		//(STATE, [INPUTS], [INTERNALS]) X (STATE, [OUTPUTS], [INTERNALS]
		StringBuilder result = new StringBuilder();
		result.append("(" + _internal_vars.getVariable("currentState").toString() + ", [ ");
		//inputs
		if(_inputs != null){
			for(Entry<String, ComChannel<?>> input : _inputs.entrySet()) {
				if(input.getValue().value() != null
						&& (!(input.getValue().value() instanceof Boolean) || (Boolean)input.getValue().value())
						&& (!(input.getValue().value() instanceof Integer) || (Integer)input.getValue().value() != 0))
					result.append(input.toString() + ", ");
			}
		}
		result.append(" ], [ ");
		//internals
		for(Entry<String, Object> variable : _internal_vars.getAllVariables().entrySet()){
			if(variable.getKey().equals("currentState"))
				continue;
			if(variable.getValue() != null
					&& (!(variable.getValue() instanceof Boolean) || (Boolean)variable.getValue())
					&& (!(variable.getValue() instanceof Integer) || (Integer)variable.getValue() != 0))
				result.append(variable.toString() + ", ");
		}
				
		result.append(" ]) ->\n\t\t (" + _endState.toString() + ", [ ");
		if(_outputs != null){
			for(Entry<String, Object> output : _temp_outputs.entrySet()) {
				if(output.getValue() != null)
//						&& (!(output.getValue() instanceof Boolean) || (Boolean)output.getValue())
//						&& (!(output.getValue() instanceof Integer) || (Integer)output.getValue() != 0))
					result.append(output.toString() + ", ");
			}
		}
		result.append(" ], [ ");
		//internals
		for(Entry<String, Object> variable : _temp_internal_vars.entrySet()){
			if(variable.getValue() != null)
//					&& (!(variable.getValue() instanceof Boolean) || (Boolean)variable.getValue())
//					&& (!(variable.getValue() instanceof Integer) || (Integer)variable.getValue() != 0))
				result.append(variable.toString() + ", ");
		}
		result.append(" ])");
		return result.toString();
	}

//	public int getWorkload() {
//		int workload = 0;
//		ArrayList<Type> types = new ArrayList<Type>();
//		for(Entry<String, ComChannel<?>> input : _inputs.entrySet()){
//			if(input.getValue().value() != null){
//				if(types.contains(input.getValue()._type)){
//					workload+=5;
//				}else{
//					types.add(input.getValue()._type);
//				}
//				if(input.getValue().value() instanceof Boolean && !(Boolean) input.getValue().value()){
//					continue;
//				} else if(input.getValue().value() instanceof Integer && ((Integer)input.getValue().value()) == 0){
//					continue;
//				}
//				workload++;
//			}
//		}
//		return workload;
//	}
	
	@Override
	public ComChannelList getInputChannels()
	{
		return _inputs;
	}
	
	@Override
	public ComChannelList getOutputChannels()
	{
		return _outputs;
	}
	
	@Override
	public HashMap<String, Object> getTempOutputChannels()
	{
		return _temp_outputs;
	}

	@Override
	public void setIndex(int index) {
		_transition_number = index;
	}
	
	@Override
	public int getIndex()
	{
		return _transition_number;
	}

}
