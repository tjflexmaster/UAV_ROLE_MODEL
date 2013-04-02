package NewModel.Roles;


import java.util.ArrayList;
import java.util.LinkedList;

import NewModel.Simulation.Simulator;
import NewModel.Utils.RoleEvent;
import NewModel.Events.*;

public abstract class Role implements IRole {

	private RoleType _type;
	
	private LinkedList<RoleEvent> _state_history = new LinkedList<RoleEvent>();
	private RoleState _state = RoleState.STARTING;
	private RoleState _next_state = RoleState.STARTING;
	private int _next_state_time = 1;
	

	/**
	 * This method contains the logic which the role executes
	 * when it needs to move to the next state.
	 * Everytime the simulator advances to the next time step it calls
	 * this method first.  The role should only allow this method to be
	 * processed if nextStateTime == Simulator.time().  If it is then 
	 * this method is processed.  This implies that the role is immediately
	 * placed into its new state.
	 */
	public abstract boolean processNextState();
	
	
	/**
	 * This method contains the logic which the role executes when global state
	 * has changed and the role may need to do something.  This is executed after
	 * processNextState.  If the role needs to respond to some global state it 
	 * must do it inside this method.
	 */
	public abstract void updateState();
	
	
	/**
	 * This method contains the logic for handling external events.  A list of events
	 * is given to the role and the role then decides how it is effected by each event.
	 * Each event has an event type and a duration.
	 * @param events
	 */
	public abstract void processEvents(ArrayList<Event> events);
	

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
			System.out.println(type().name() + " no next state!");
		} else if ( time > 0 ) {
			_next_state = state;
			_next_state_time = Simulator.getTime() + time;
			System.out.println(type().name() + " next state: " + state.name() + " at time: " + _next_state_time);
		} else {
//			System.out.println("Failed to set the next state");
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
