package NewModel.Simulation;

import java.util.ArrayList;
import java.util.HashMap;

import NewModel.Events.Event;
import NewModel.Roles.EnvironmentRole;
import NewModel.Roles.IRole;
import NewModel.Roles.MissionManagerRole;
import NewModel.Roles.ParentSearchRole;
import NewModel.Roles.PilotRole;
import NewModel.Roles.RoleState;
import NewModel.Roles.RoleType;
import NewModel.Roles.UAVGUIRole;
import NewModel.Roles.UAVRole;
import NewModel.Roles.VideoAnalystRole;
import NewModel.Roles.VideoGUIRole;

public class DefaultTeam {

	private HashMap<RoleType, IRole> _roles = new HashMap<RoleType, IRole>();
	
	public DefaultTeam()
	{
		_roles.put(RoleType.ROLE_ENVIRONMENT, new EnvironmentRole());
		_roles.put(RoleType.ROLE_UAV, new UAVRole());
		_roles.put(RoleType.ROLE_UAV_GUI, new UAVGUIRole());
		_roles.put(RoleType.ROLE_PARENT_SEARCH, new ParentSearchRole());
		_roles.put(RoleType.ROLE_MISSION_MANAGER, new MissionManagerRole());
		_roles.put(RoleType.ROLE_PILOT, new PilotRole());
		_roles.put(RoleType.ROLE_VIDEO_ANALYST, new VideoAnalystRole());
		_roles.put(RoleType.ROLE_VIDEO_GUI, new VideoGUIRole());
	}
	
	
	public RoleState getRoleState(RoleType type) 
	{
		return _roles.get(type).state();
	}
	
	public int getNextStateTime(int time) 
	{
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
	public boolean processNextState()
	{
		boolean result = false;
		for( IRole role : _roles.values() ) {
			if ( role.processNextState() ) {
				result = true;
			}
		}
		
		return result;
	}
	
	
	public void updateState()
	{
		//Have each role look at the global state and update itself
		for( IRole role : _roles.values() ) {
			role.updateState();
		}
	}
	
	public void processExternalEvents(ArrayList<Event> events)
	{
		//Have each role look at the events and see how it is effected
		for( IRole role : _roles.values() ) {
			role.processEvents(events);
		}
	}
}
