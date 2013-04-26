<<<<<<< Upstream, based on origin/master
package CUAS.Simulator;


import java.util.ArrayList;

import CUAS.Simulator.IStateEnum;

public abstract class Actor extends State implements IActor, IObservable {

	private String _name;
	
	/**
	 * Roles must also define their own Inputs and Outputs.  This is done by extending the
	 * IInput and IOutput interfaces with enums.
	 * ex:
	 * public enum Inputs implements IInput
	 */
	
	/**
	 * Used to handle inputs and outputs for the Role
	 */
	protected ArrayList<IData> _input = new ArrayList<IData>();
	protected ArrayList<IData> _output = new ArrayList<IData>();
	
	
	/**
	 * IActor Methods
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
	public abstract ArrayList<IData> processNextState();
	
	
	/**
	 * This method contains the logic which the role executes when global state
	 * has changed and the role may need to do something.  This is executed after
	 * processNextState.  If the role needs to respond to some global state it 
	 * must do it inside this method.
	 * @return 
	 */
	public abstract ArrayList<IData> processInputs();
	
	
	
	/**
	 * Private methods for changing the state
	 */
	protected void name(String name)
	{
		_name = name;
	}
	
	public String name()
	{
		return _name;
	}
	
	public void addInput(IData input)
	{
		_input.add(input);
	}
	
	public void addInputs(ArrayList<IData> inputs)
	{
		_input.addAll(inputs);
	}
	
	
	public ArrayList<IData> getObservable()
	{
		return _output;
	}
	
	
}
=======
package CUAS.Simulator;


import java.util.ArrayList;

import CUAS.Simulator.IStateEnum;
import NewModel.Events.IEvent;

public abstract class Actor implements IActor, IObservable {

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
	protected ArrayList<IData> _input = new ArrayList<IData>();
	protected ArrayList<IData> _output = new ArrayList<IData>();
	
	
	/**
	 * IActor Methods
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
	public abstract ArrayList<IData> processNextState();
	
	
	/**
	 * This method contains the logic which the role executes when global state
	 * has changed and the role may need to do something.  This is executed after
	 * processNextState.  If the role needs to respond to some global state it 
	 * must do it inside this method.
	 * @return 
	 */
	public abstract ArrayList<IData> processInputs();
	
	
	
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
	
	public void addInput(IData input)
	{
		_input.add(input);
	}
	
	public void addInputs(ArrayList<IData> inputs)
	{
		_input.addAll(inputs);
	}
	
	
	public ArrayList<IData> getObservable()
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
			if ( sim().debug() )
				System.out.println(name() + " no next state!");
		} else if ( time > 0 ) {
			_next_state = state;
			_next_state_time = Simulator.getInstance().getTime() + time;
			if ( sim().debug() )
				System.out.println(name() + " next state: " + state.name() + " at time: " + _next_state_time);
		} else {
//			System.out.println("Failed to set the next state");
		}
	}
	
	protected void state(IStateEnum state)
	{
		_state = state;
		if ( sim().debug() )
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
	protected Simulator sim()
	{
		return Simulator.getInstance();
	}
}
>>>>>>> 4175940 Edited things to fit new schema
