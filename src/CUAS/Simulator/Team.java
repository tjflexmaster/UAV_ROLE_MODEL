package CUAS.Simulator;

import java.util.HashMap;


public abstract class Team implements ITeam {

	protected HashMap<String, IActor> _actors = new HashMap<String, IActor>();
	
	@Override
	public IActor getActor(String actor_name)
	{
		IActor result = _actors.get(actor_name);
		assert result != null : "Actor: " + actor_name + " is not part of the team.";
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

}
