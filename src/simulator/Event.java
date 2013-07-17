package simulator;

import java.util.HashMap;

public abstract class Event implements IEvent, IActor {
	
	private int _count = 0;
	protected String _name;
	protected ITransition _transition;
	
	private ActorVariableWrapper _internal_vars = new ActorVariableWrapper();
	
	/**
	 * This method returns an enabled transition.  Events only have a single transition.
	 * @return
	 */
	public abstract ITransition getEnabledTransition();
	
	public void setEventCount(int count)
	{
		if ( count > 0 )
			_count = count;
		else
			_count = 0;
	}
	
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
		_count = Math.max(0, _count--);
	}
	
	public String name()
	{
		return _name;
	}
	
	public HashMap<IActor, ITransition> getTransitions()
	{
		HashMap<IActor, ITransition> result = new HashMap<IActor, ITransition>();
		
		result.put(this, getEnabledTransition());
		return result;
	}
	
	protected ActorVariableWrapper getInternalVars()
	{
		return _internal_vars;
	}
	
	protected State getState()
	{
		return (State) _internal_vars.getVariable("currentState");
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
