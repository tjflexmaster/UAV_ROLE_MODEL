package NewModel.Simulation;


import NewModel.Roles.EnvironmentRole;
import NewModel.Roles.MissionManagerRole;
import NewModel.Roles.ParentSearchRole;
import NewModel.Roles.PilotRole;
import NewModel.Roles.RoleType;
import NewModel.Roles.UAVGUIRole;
import NewModel.Roles.UAVRole;
import NewModel.Roles.VideoAnalystRole;
import NewModel.Roles.VideoGUIRole;

public class DefaultTeam extends Team {

//	private HashMap<RoleType, IRole> _roles = new HashMap<RoleType, IRole>();
	
	public DefaultTeam()
	{
//		_roles.put(RoleType.ROLE_ENVIRONMENT, new EnvironmentRole());
		_roles.put(RoleType.ROLE_UAV, new UAVRole());
		_roles.put(RoleType.ROLE_UAV_GUI, new UAVGUIRole());
		_roles.put(RoleType.ROLE_PARENT_SEARCH, new ParentSearchRole());
		_roles.put(RoleType.ROLE_MISSION_MANAGER, new MissionManagerRole());
		_roles.put(RoleType.ROLE_PILOT, new PilotRole());
//		_roles.put(RoleType.ROLE_VIDEO_ANALYST, new VideoAnalystRole());
//		_roles.put(RoleType.ROLE_VIDEO_GUI, new VideoGUIRole());
	}
}
