package CUAS.Simulator;


public abstract class Actor extends State implements IActor {

	private String _name;
	
	/**
	 * Actors define their output, and state processing.
	 */
	
	
	/**
	 * IActor Methods
	 */
	
	/**
	 * nextStateTime is implemented in the State class
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
	public abstract void processNextState();
	
	
	/**
	 * This method contains the logic which the actor executes when global state
	 * has changed and the actor may need to do something.  This is executed after
	 * processNextState.  If the role needs to respond to some global state it 
	 * must do it inside this method.
	 * @return 
	 */
	public abstract void processInputs();
	
	public String name()
	{
		return _name;
	}
	
	
	/**
	 * Only the class itself can set it's name
	 */
	protected void name(String name)
	{
		_name = name;
	}
	
}
