package NewModel.Roles;


import java.util.ArrayList;

import NewModel.Events.IEvent;
import NewModel.Simulation.ICommunicate;
import NewModel.Simulation.IInputEnum;
import NewModel.Simulation.IOutputEnum;
import NewModel.Simulation.IRole;
import NewModel.Simulation.IStateEnum;
import NewModel.Simulation.Simulator;

public abstract class Role implements IRole, ICommunicate {

	private String _name;
	
//	private LinkedList<RoleEvent> _state_history = new LinkedList<RoleEvent>();
	private IStateEnum _state = null;
	private IStateEnum _next_state = null;
	private int _next_state_time = 1;
	
	/**
	 * Roles must also define their own Inputs and Outputs.  This is done by extending the
	 * IInput and IOutput interfaces with enums.
	 * ex:
	 * public enum Inputs implements IInput
	 */
	
	/**
	 * Used to handle inputs and outputs for the Role
	 */
	protected ArrayList<IInputEnum> _input = new ArrayList<IInputEnum>();
	protected ArrayList<IOutputEnum> _output = new ArrayList<IOutputEnum>();
	
	
	/**
	 * IRole Methods
	 */
	
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
	public abstract void processEvent(IEvent event);
	
	public int nextStateTime()
	{
		return _next_state_time;
	}
	
	public IStateEnum nextState()
	{
		return _next_state;
	}
	
	public IStateEnum state()
	{
		return _state;
	}
	
	public String name()
	{
		return _name;
	}
	
	
	
	
	
	/**
	 * ICommunicate Methods
	 */
	
	public void addInput(IInputEnum input)
	{
		_input.add(input);
	}
	
	public void addInputs(ArrayList<IInputEnum> inputs)
	{
		_input.addAll(inputs);
	}
	
	public ArrayList<IOutputEnum> getOutput()
	{
		return _output;
	}
	
	

	
	/**
	 * Private methods for changing the state
	 */
	protected void name(String name)
	{
		_name = name;
	}
	
	protected void nextState(IStateEnum state, int time)
	{
		if ( state == null ) {
			_next_state = null;
			_next_state_time = 0;
			if ( simulator().debug() )
				System.out.println(name() + " no next state!");
		} else if ( time > 0 ) {
			_next_state = state;
			_next_state_time = Simulator.getInstance().getTime() + time;
			if ( simulator().debug() )
				System.out.println(name() + " next state: " + state.name() + " at time: " + _next_state_time);
		} else {
//			System.out.println("Failed to set the next state");
		}
	}
	
	protected void state(IStateEnum state)
	{
		_state = state;
		if ( simulator().debug() )
			System.out.println(name() + " changed to " + state.name());
//		recordCurrentState();
	}
	
//	protected void recordCurrentState()
//	{
//		//Record the state
//		_state_history.add(new RoleEvent(_state));
//	}
	
	protected void resetNextState()
	{
		_next_state_time = 0;
		_next_state = null;
	}
	
	/**
	 * Convenience method so that getInstance does not have to be called over and over
	 * @return
	 */
	protected Simulator simulator()
	{
		return Simulator.getInstance();
	}
}
