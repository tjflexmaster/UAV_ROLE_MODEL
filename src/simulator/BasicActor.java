package simulator;

import java.util.ArrayList;
import java.util.HashMap;

public class BasicActor extends Actor {

	@Override
	protected void initializeInternalVariables() {
		_internal_vars = new ActorVariableWrapper();

	}
	
	protected void setInternalVariables(ActorVariableWrapper internals){
		_internal_vars = internals;
	}
	protected void addVariable(String name, Object o){
		_internal_vars.addVariable(name, o);
	}
	
	protected void setName(String name){
		_name = name;
	}

	@Override
	public HashMap<IActor, ITransition> getTransitions() {
		State state = this.getCurrentState();
		ArrayList<ITransition> enabledTransitions = state.getEnabledTransitions();
		if(enabledTransitions.size() == 0)
			return null;
		ITransition nextTransition = enabledTransitions.get(0);
		for(ITransition t : enabledTransitions){
			if(nextTransition.priority() < t.priority()){
				nextTransition = t;
			}
		}
		HashMap<IActor, ITransition> transitions = new HashMap<IActor, ITransition>();
		transitions.put(this, nextTransition);
		return transitions;
	}

}
