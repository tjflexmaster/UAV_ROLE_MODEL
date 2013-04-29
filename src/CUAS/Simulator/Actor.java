package CUAS.Simulator;


import java.util.ArrayList;

import WiSAR.Agents.ParentSearch.Outputs;

public abstract class Actor extends State implements IActor, IObservable {

	private String _name;
	
	/**
	 * Actors define their output, observables, and state processing.
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
	public abstract void processNextState();
	
	
	/**
	 * This method contains the logic which the actor executes when global state
	 * has changed and the actor may need to do something.  This is executed after
	 * processNextState.  If the role needs to respond to some global state it 
	 * must do it inside this method.
	 * @return 
	 */
	public abstract void processInputs();
	
	
	
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
	
	public void addInput(ArrayList<IData> inputs)
	{
		_input.addAll(inputs);
	}
	
	protected void addObservation(IData data)
	{
		_output.add(data);
	}
	
	protected void clearObservations()
	{
		_output.clear();
	}
	
	
	public ArrayList<IData> getObservations()
	{
		return _output;
	}
	
	
}
