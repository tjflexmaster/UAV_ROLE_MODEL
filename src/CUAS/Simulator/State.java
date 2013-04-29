package CUAS.Simulator;

public abstract class State {

	private IStateEnum _state = null;
	private IStateEnum _next_state = null;
	private int _next_state_time = 1;
	
	public int nextStateTime()
	{
		return _next_state_time;
	}
	
	protected IStateEnum nextState()
	{
		return _next_state;
	}
	
	protected IStateEnum state()
	{
		return _state;
	}
	
	protected void nextState(IStateEnum state, int time)
	{
		if ( state == null ) {
			_next_state = null;
			_next_state_time = 0;
		} else if ( time > 0 ) {
			_next_state = state;
			_next_state_time = sim().getTime() + time;
			
			if ( sim().debug() )
				System.out.println("Next State: " + _next_state.name() + " at time: " + _next_state_time);
		} else {
//			System.out.println("Failed to set the next state");
		}
	}
	
	protected void state(IStateEnum state)
	{
		_state = state;
	}
	
	protected void resetNextState()
	{
		_next_state_time = 0;
		_next_state = null;
	}
	
	/**
	 * Convenience method so that getInstance does not have to be called over and over
	 * @return
	 */
	protected Simulator sim()
	{
		return Simulator.getInstance();
	}
}
