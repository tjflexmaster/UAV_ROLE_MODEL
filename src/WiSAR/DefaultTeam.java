package WiSAR;


import CUAS.Simulator.Team;
import WiSAR.AgentEnum.MissionManagerRole;
import WiSAR.AgentEnum.ParentSearch;
import WiSAR.AgentEnum.Roles;

public class DefaultTeam extends Team {

	public DefaultTeam()
	{
		_roles.put(ObjectEnum.PARENT_SEARCH.name(), new ParentSearch());
		_roles.put(AgentEnum.MISSION_MANAGER.name(), new MissionManagerRole());
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
