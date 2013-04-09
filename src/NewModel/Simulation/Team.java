package NewModel.Simulation;

import java.util.ArrayList;
import java.util.HashMap;

import NewModel.Events.Event;
import NewModel.Roles.IRole;
import NewModel.Roles.RoleState;
import NewModel.Roles.RoleType;

public abstract class Team implements ITeam {

	protected HashMap<RoleType, IRole> _roles = new HashMap<RoleType, IRole>();
	
	@Override
	public RoleState getRoleState(RoleType type) {
		return _roles.get(type).state();
	}

	@Override
	public int getNextStateTime(int time) {
		int min = 0;
		for( IRole role : _roles.values() ) {
			int next_time = role.nextStateTime();
			
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
	public boolean processNextState() {
		boolean result = false;
		for( IRole role : _roles.values() ) {
			if ( role.processNextState() ) {
				result = true;
			}
		}
		
		return result;
	}

	@Override
	public void updateState() {
		//Have each role look at the global state and update itself
		for( IRole role : _roles.values() ) {
			role.updateState();
		}
	}

	@Override
	public void processExternalEvents(ArrayList<Event> events) {
		//Have each role look at the events and see how it is effected
		for( IRole role : _roles.values() ) {
			role.processEvents(events);
		}
	}

}
