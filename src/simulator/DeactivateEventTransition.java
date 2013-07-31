package simulator;

import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

public class DeactivateEventTransition implements ITransition {

	protected ComChannelList _inputs;
	private ComChannelList _outputs;
	private HashMap<String, Object> _temp_outputs;
	protected ActorVariableWrapper _internal_vars;
	protected State _end_state;
	
	public DeactivateEventTransition (ComChannel<?> output_channel, ActorVariableWrapper internal_vars, State end_state) {
		
		_inputs = new ComChannelList();
		
		_outputs = new ComChannelList();
		_outputs.add(output_channel);
		_temp_outputs = new HashMap<String, Object>();
		_temp_outputs.put(output_channel.name(), null);
		
		_internal_vars = internal_vars;
		_end_state = end_state;
	}
	
	public DeactivateEventTransition(DeactivateEventTransition t) {
		_internal_vars = t._internal_vars;
		_end_state = t._end_state;
		_inputs = t._inputs;
		_outputs = t._outputs;
		_temp_outputs = new HashMap<String, Object>();
		_temp_outputs.putAll(t._temp_outputs);
	}

	@Override
	public void fire() {
		if(!_temp_outputs.isEmpty()){
			for(ComChannel<?> output : _outputs.values()){
				Object temp = _temp_outputs.get(output.name());
				output.set(temp);
//				_temp_outputs.put(output.name(), null);
			}
			
			//Update the actor state
			_internal_vars.setVariable("currentState", _end_state);
		}
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public Range getDurationRange() {
		return new Range();
	}

	@Override
	public int priority() {
		return 1;
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
				if(input.getValue().value() != null)
					result.append(input.toString() + ", ");
			}
		}
		result.append(" ], [ ");
		//internals
		for(Entry<String, Object> variable : _internal_vars.getAllVariables().entrySet()){
			if(variable.getKey().equals("currentState"))
				continue;
			if(variable.getValue() != null)
				result.append(variable.toString() + ", ");
		}
				
		result.append(" ]) ->\n\t\t (" + _end_state.toString() + ", [ ");
		if(_outputs != null){
			for(Entry<String, Object> output : _temp_outputs.entrySet()) {
				if(output.getValue() != null)
					result.append(output.toString() + ", ");
			}
		}
		result.append(" ], [ ])");
		return result.toString();
	}
}
