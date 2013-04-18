package WiSAR;


import NewModel.Simulation.Team;
import WiSAR.Roles.MissionManagerRole;
import WiSAR.Roles.ParentSearchRole;
import WiSAR.Roles.Roles;

public class DefaultTeam extends Team {

	public DefaultTeam()
	{
		_roles.put(Roles.PARENT_SEARCH.name(), new ParentSearchRole());
		_roles.put(Roles.MISSION_MANAGER.name(), new MissionManagerRole());
//		_roles.put(RoleType.ROLE_ENVIRONMENT, new EnvironmentRole());
//		_roles.put(RoleType.ROLE_UAV, new UAVRole());
//		_roles.put(RoleType.ROLE_UAV_GUI, new UAVGUIRole());
//		_roles.put(RoleType.ROLE_PARENT_SEARCH, new ParentSearchRole());
//		_roles.put(RoleType.ROLE_MISSION_MANAGER, new MissionManagerRole());
//		_roles.put(RoleType.ROLE_PILOT, new PilotRole());
//		_roles.put(RoleType.ROLE_VIDEO_ANALYST, new VideoAnalystRole());
//		_roles.put(RoleType.ROLE_VIDEO_GUI, new VideoGUIRole());
	}
}
