package WiSAR;


import CUAS.Simulator.Team;
import WiSAR.Agents.MissionManagerRole;
import WiSAR.Agents.OperatorGUIRole;
import WiSAR.Agents.OperatorRole;
import WiSAR.Agents.ParentSearch;
import WiSAR.Agents.UAVRole;
import WiSAR.Agents.VideoGUIRole;
import WiSAR.Agents.VideoOperatorRole;
<<<<<<< Upstream, based on origin/master
=======
import WiSAR.submodule.UAVFlightPlan;
>>>>>>> 86f791f merged with pull

public class DefaultTeam extends Team {

	public DefaultTeam()
	{
		//Add main actors
		_actors.put(Actors.PARENT_SEARCH.name(), new ParentSearch());
		_actors.put(Actors.MISSION_MANAGER.name(), new MissionManagerRole());
		_actors.put(Actors.OPERATOR.name(), new OperatorRole());
		_actors.put(Actors.OPERATOR_GUI.name(), new OperatorGUIRole());
		_actors.put(Actors.VIDEO_OPERATOR.name(), new VideoOperatorRole());
		_actors.put(Actors.VIDEO_OPERATOR_GUI.name(), new VideoGUIRole());
		_actors.put(Actors.UAV.name(), new UAVRole());
		
	}
}
