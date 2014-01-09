package simulator;

import java.util.*;

/**
 * this abstract class is extended by the actors of the simulation
 * it contains the variables and methods that compose an actor
 * @author tjr team
 *
 */
public abstract class Actor implements IActor {
	/**
	 * this variable represents the name we give to the actor
	 */
	private String _name;
	private State _currentState;
	/**
   * this represents all of the subactors that this actor holds
   */
  private ArrayList<Actor> _subactors = null;
	
	private ArrayList<IState> _states = new ArrayList<IState>();
	
//	/**
//	 * this represents the current state of the actor (state machine)
//	 */
//	protected ActorVariableWrapper _internal_vars = new ActorVariableWrapper();
	
//	abstract protected void initializeInternalVariables();
	
	/**
	 * This method must be implemented by the Actor.  
	 * @return
	 */
	public HashMap<IActor, ITransition> getTransitions(){
		
		State state = this.getCurrentState();
		ArrayList<ITransition> enabledTransitions = state.getEnabledTransitions();
		
		//Log this with JPF
		MetricManager.instance().setDecisionWorkload(Simulator.getSim().getClockTime(), this.name(), this.getCurrentState().getName(), enabledTransitions.size());
		
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
	
	
	
	
	/**
	 * 
	 * @return return the current state of the actor
	 */
	public State getCurrentState() {
	  return _currentState;
	}
	
	public ArrayList<IState> getStates() {
	  return _states;
	}
	
	public String name()
	{
		return name();
	}
	
	protected ArrayList<Actor> getSubActors()
	{
		return _subactors;
	}
	
	protected void addSubActor(Actor a)
	{
		_subactors.add(a);
	}

	
	
	/**
	 * HELPER METHODS
	 */
	protected void setName(String name){
		_name = name;
	}
	protected void startState(IState state)
	{
		assert _states.contains(state):"Start state not available.";
		_currentState = (State) state;
	}
	
	protected Actor add(IState state)
	{
		if ( _states.contains(state) )
			return this;
		
		_states.add(state);
		return this;
	}
	
//	public int getWorkload(){
//		int temp_workload = 0;
//		temp_workload += _internal_vars.getWorkload();
//		temp_workload += getCurrentState().getWorkload();
//		
//		if(getCurrentState().equals("IDLE")){
//			_workload = 0;
//		}else{
//			_workload ++;
//		}
//		
//		return _workload + temp_workload;
//	}

	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Actor other = (Actor) obj;
		if (name() == null) {
			if (other.name() != null)
				return false;
		} else if (!name().equals(other.name()))
			return false;
		return true;
	}
	

	@Override
	public int hashCode()
	{
		return name().hashCode();
	}
	
	/**
	 * this method works like a normal toSTring method
	 * @return return the string representation of the actor
	 */
	public String toString() {
		return name();
	}
}
