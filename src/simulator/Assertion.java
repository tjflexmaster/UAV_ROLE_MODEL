package simulator;

import java.util.HashMap;
import java.util.Map.Entry;

public class Assertion {
		
		protected ComChannelList _inputs;
		protected ActorVariableWrapper _internal_vars;
		protected HashMap<String, DataComparator> _input_checks;
		protected HashMap<String, DataComparator> _internal_checks;
		protected String _message;


		/**
		 * a transition is used by an agent (state machine) to formally define state transitions 
		 * @param input_prereqs 
		 * @param internal_prereqs 
		 * @param input the necessary input an agent needs before it can make the transition
		 * @param endState the new state the agent will move to
		 * @param output the output the transition produces
		 * @param curState the current state of the actor
		 * @param priority the priority level of the transition
		 * todo add a duration that represents how long it takes to move between states
		 * @return 
		 */
		public Assertion (ActorVariableWrapper internalVars, ComChannelList inputs,
				HashMap<String, DataComparator> input_prereqs, HashMap<String, DataComparator> internal_prereqs, String message) {
			
			_inputs = new ComChannelList();
			for(Entry<String, ComChannel<?>> chan : inputs.entrySet()){
				_inputs.add(chan.getValue());
			}
			for(Entry<String, DataComparator> check : input_prereqs.entrySet()){
				_input_checks.put(check.getKey(), check.getValue());
			}
			for(Entry<String, DataComparator> check : internal_prereqs.entrySet()){
				_internal_checks.put(check.getKey(), check.getValue());
			}
			_internal_vars = internalVars;
			_message = message;
		}
		/**
		 * @return return whether the transition can be made based on the state of the ComChannels
		 */
		public void checkAssertion() {
			for(Entry<String, DataComparator> check : _input_checks.entrySet()){
				ComChannel input = _inputs.get(check.getKey());
				assert check.getValue().isTrue(input.value())
					: _message ;
			}
			for(Entry<String, DataComparator> check : _internal_checks.entrySet()){
				Object input = _internal_vars.getVariable(check.getKey());
				assert check.getValue().isTrue(input)
					: _message;
			}
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
			return result.toString();
		}

	

}
