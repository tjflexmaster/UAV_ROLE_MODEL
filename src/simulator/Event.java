package simulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class Event implements IActor {
	
	private int _count = 0;
	protected String _name;
	private ArrayList<IState> _states = new ArrayList<IState>();
	protected ActorVariableWrapper _internal_vars = new ActorVariableWrapper();
//	protected ComChannelList _outputs;
	
	Event(String name, int count, final HashMap<ComChannel<?>, IPredicate> inputs, ComChannel<?> output_channel, Object output_value)
	{
		this._name = name;
		this.setEventCount(count);
		
		//Build the states and transitions for the event
		State active = new State("active");
		State inactive = new State("inactive");
		
		
		ActivateEventTransition t = new ActivateEventTransition(inputs.keySet(), output_channel, output_value, _internal_vars, inactive) {
			@Override
			public boolean isEnabled()
			{
				for(Entry<ComChannel<?>, IPredicate> input : inputs.entrySet()) {
					IPredicate p = input.getValue();
					ComChannel<?> c = input.getKey();
					if ( !p.evaluate(c.value()) )
						return false;
				}
				
				return true;
			}
		};
		active.add(t);
		
		DeactivateEventTransition d = new DeactivateEventTransition(output_channel, _internal_vars, active);
		inactive.add(d);
		_states.add(active);
		_states.add(inactive);
		_internal_vars.setVariable("currentState", active);
	}
	
	public void setEventCount(int count)
	{
		if ( count > 0 )
			_count = count;
		else
			_count = 0;
	}
	
//	public void deactivate(){
//		for(Entry<String, ComChannel<?>> c : _outputs.entrySet()){
//			c.getValue().set(null);
//		}
//	}
	
	public int getEventCount()
	{
		return _count;
	}
	
	public boolean isFinished()
	{
		if ( _count > 0 )
			return false;
		else
			return true;
	}
	
	public void incrementCount()
	{
		_count++;
	}
	
	public void decrementCount()
	{
		_count = Math.max(0, --_count);
	}
	
	public String name()
	{
		return _name;
	}
	
	public State getCurrentState() {
		return (State) _internal_vars.getVariable("currentState");
	}
	
	public HashMap<IActor, ITransition> getTransitions()
	{
		State state = this.getCurrentState();
		ArrayList<ITransition> enabledTransitions = state.getEnabledTransitions();
		if(enabledTransitions.size() == 0)
			return null;
		ITransition nextTransition = enabledTransitions.get(0);
		HashMap<IActor, ITransition> transitions = new HashMap<IActor, ITransition>();
		transitions.put(this, nextTransition);
		return transitions;
		
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Actor other = (Actor) obj;
		if (_name == null) {
			if (other.name() != null)
				return false;
		} else if (!_name.equals(other.name()))
			return false;
		return true;
	}
	

	@Override
	public int hashCode()
	{
		return _name.hashCode();
	}
}
