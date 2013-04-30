package WiSAR;


import CUAS.Simulator.Team;
import WiSAR.Agents.MissionManagerRole;
import WiSAR.Agents.OperatorGUIRole;
import WiSAR.Agents.OperatorRole;
import WiSAR.Agents.ParentSearch;
import WiSAR.Agents.UAVRole;
import WiSAR.Agents.VideoGUIRole;
import WiSAR.Agents.VideoOperatorRole;
import WiSAR.submodule.UAVBattery;

public class DefaultTeam extends Team {

	public DefaultTeam()
	{
		_actors.put(Actors.PARENT_SEARCH.name(), new ParentSearch());
		_actors.put(Actors.MISSION_MANAGER.name(), new MissionManagerRole());
		_actors.put(Actors.OPERATOR.name(), new OperatorRole());
		_actors.put(Actors.OPERATOR_GUI.name(), new OperatorGUIRole());
		_actors.put(Actors.VIDEO_OPERATOR.name(), new VideoOperatorRole());
		_actors.put(Actors.VIDEO_OPERATOR_GUI.name(), new VideoGUIRole());
		_actors.put(Actors.UAV.name(), new UAVRole());
		_actors.put(Actors.UAV_BATTERY.name(), new UAVBattery());
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
