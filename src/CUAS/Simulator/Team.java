package CUAS.Simulator;

import java.util.HashMap;


public abstract class Team implements ITeam {

	protected HashMap<String, IActor> _actors = new HashMap<String, IActor>();
	
	/**
	 * Link a child actor to a parent actor.  A child actor can only be linked to a single parent.
	 */
	protected HashMap<String, String> _registered_child_actors = new HashMap<String, String>();
	
	/**
	 * This team implementation allows for child actors, these child actors receive input through
	 * the parent actor and are observable through the parent actor.
	 */
	
	@Override
	public IActor getActor(String actor_name)
	{
		if ( !_actors.containsKey(actor_name) ) {
			if ( _registered_child_actors.containsKey(actor_name) ) {
				actor_name = _registered_child_actors.get(actor_name);
			} else {
				assert false : "Actor: " + actor_name + " not found!";
			}
		}
		IActor result = _actors.get(actor_name);
		assert result != null : "Actor: " + actor_name + " is registered incorrectly.";
		return result;
	}

	@Override
	public int getNextStateTime(int time) {
		int min = 0;
		for( IActor actor : _actors.values() ) {
			int next_time = actor.nextStateTime();
			
			if ( next_time > time ) {
				if ( min == 0 ) {
					min = next_time;
				} else if ( next_time < min ) {
					min = next_time;
				}
			}
		}
		
		return min;
	}

	
	/**
	 * Every Role on the team processes the Next State.
	 * If they didn't have a next state for this time step
	 * they will return false.  We should only be processing because
	 * one of the roles has a next state at this point.
	 * @return boolean
	 */
	@Override
	public void processNextState() {
		for( IActor actor : _actors.values() ) {
			actor.processNextState();
		}
	}

	@Override
	public void processInputs() {
		
		//Have each actor process its inputs
		for( IActor actor : _actors.values() ) {
			actor.processInputs();
		}
		
	}
	
	/**
	 * This method allows a child actor to be referenced through its parent.
	 * Whenever the getActor method is called using the child name it will return the parents name.
	 * This allows the simulator to deliver input and output properly for child actors which are not
	 * part of the main team.
	 * 
	 * A child can only be linked to a single parent
	 * 
	 * @param parent
	 * @param child
	 */
	public void registerChildActor(String parent, String child)
	{
		if ( !_actors.containsKey(parent) ) 
			assert false : "Cannot register: " + child + " to parent: " + parent + " because parent does not exist!";
		
		_registered_child_actors.put(child, parent);
		Simulator.getInstance().linkInput(parent, child);
		Simulator.getInstance().linkObservations(parent, child);
	}
	
}
