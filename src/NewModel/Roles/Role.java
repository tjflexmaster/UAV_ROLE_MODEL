package NewModel.Roles;


import java.util.LinkedList;

import NewModel.Simulation.Simulator;
import NewModel.Utils.RoleEvent;

public abstract class Role implements IRole {

	private RoleType _type;
	
	private LinkedList<RoleEvent> _state_history = new LinkedList<RoleEvent>();
	private RoleState _state = RoleState.STARTING;
	private RoleState _next_state = RoleState.STARTING;
	private int _next_state_time = 1;
	

	/**
	 * IMPLEMENT THE LOGIC FOR THE STATE
	 */
	public abstract void updateState();

	public abstract boolean processNextState();
	
	public int nextStateTime()
	{
		return _next_state_time;
	}
	
	public RoleState nextState()
	{
		return _next_state;
	}
	
	public RoleState state()
	{
		return _state;
	}
	
	public RoleType type()
	{
		return _type;
	}
	
	/**
	 * Private methods for changing the state
	 */
	protected void type(RoleType type)
	{
		_type = type;
	}
	
	protected void nextState(RoleState state, int time)
	{
		if ( state == null ) {
			_next_state = null;
			_next_state_time = 0;
		} else if ( time > 0 ) {
			_next_state = state;
			_next_state_time = Simulator.getTime() + time;
			System.out.println(type().name() + " next state: " + state.name() + " at time: " + _next_state_time);
		} else {
			System.out.println("Failed to set the next state");
		}
	}
	
	protected void state(RoleState state)
	{
		_state = state;
		System.out.println(type().name() + " changed to " + state().name());
		recordCurrentState();
	}
	
	protected void recordCurrentState()
	{
		//Record the state
		_state_history.add(new RoleEvent(_state));
	}
	
	protected void resetNextState()
	{
		_next_state_time = 0;
		_next_state = RoleState.IDLE;
	}
	
}
