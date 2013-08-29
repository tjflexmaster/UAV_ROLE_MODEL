package simulator;

import java.util.ArrayList;

/**
 * this class represents a the state of an actor (state machine)
 * @author tjr team
 * 
 */
public class State implements IState {
	
	/**
	 * this is the name of the state
	 */
	private String _name;
	private ArrayList<ITransition> _transitions;
	/**
	 * this constructor is used for creating new states
	 * @param name
	 */
	public State(String name) {
		_name = name;
		_transitions = new ArrayList<ITransition>();
	}
	
	public State add(ITransition new_transition)
	{
		if(_transitions.contains(new_transition)){
			return this;
		}
		
		//The Actor will decide how to handle the transition and how to sort them.
		_transitions.add(new_transition);
		new_transition.setIndex(_transitions.indexOf(new_transition));
		
		return this;
	}
	
	@Override
	public ArrayList<ITransition> getEnabledTransitions() {
		//TODO Send state to the metric manager
//		Simulator.getSim()._metrics.currentKey.setState(this.getName());
		
		ArrayList<ITransition> enabled = new ArrayList<ITransition>();
		for (int i = 0; i < _transitions.size(); i++) {//ITransition t : _transitions) {
			ITransition t = _transitions.get(i);
			
			//TODO send transition to the metric manager
//			Simulator.getSim()._metrics.currentKey.setTransition(t.getIndex());
			
			
			if ( t.updateTransition() ) {
				//Copy the transition if it is enabled
				enabled.add((ITransition) new Transition((Transition)t));
				
				//TODO send enabled metric
//				Simulator.getSim().addMetric(MetricEnum.ENABLED, "TRANSITION");
			}
		}
		return enabled;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_name == null) ? 0 : _name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if(obj instanceof String && _name.equals(obj))
			return true;
		if (!(obj instanceof State))
			return false;
		State other = (State) obj;
		if (_name == null) {
			if (other._name != null)
				return false;
		} else if (!_name.equals(other._name))
			return false;
		return true;
	}
	
	@Override
	public String getName()
	{
		return _name;
	}

	/**
	 * this method works like a normal toString method
	 * @return return the string representation of this state
	 */
	public String toString() {
		return _name;
	}

//	public int getWorkload() {
//		int temp_workload = _workload + ((Transition)_transitions.get(0)).getWorkload();
//		return temp_workload;
//	}

}
