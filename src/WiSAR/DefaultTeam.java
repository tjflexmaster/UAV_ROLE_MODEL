package WiSAR;


import CUAS.Simulator.Team;
import WiSAR.Agents.MissionManagerRole;
import WiSAR.Agents.ParentSearch;

public class DefaultTeam extends Team {

	public DefaultTeam()
	{
		_actors.put(ObjectEnum.PARENT_SEARCH.name(), new ParentSearch());
//		_actors.put(AgentEnum.MISSION_MANAGER.name(), new MissionManagerRole());
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
