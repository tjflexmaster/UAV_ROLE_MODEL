package WiSAR.Events;

import java.util.ArrayList;

import CUAS.Simulator.Event;
import CUAS.Simulator.IData;
import WiSAR.Actors;
import WiSAR.Durations;
import WiSAR.Agents.UAVRole;
import WiSAR.Agents.VideoGUIRole;
<<<<<<< Upstream, based on master
import WiSAR.submodule.UAVVideoFeed;
=======
import WiSAR.submodule.UAVSignal;
>>>>>>> 7f3b80c Added the qualifier that A target sighting event cannot go active while the signal is out of range.

public class TargetSightingTrueEvent extends Event {

	public enum Outputs implements IData{
		TARGET_SIGHTED_TRUE,
		TARGET_SIGHTED_END,
	}

	public TargetSightingTrueEvent(int count) {
		_count = count;
	}
	@Override
	protected boolean eventPossible() {
		ArrayList<IData> uav_data = sim().getObservations(Actors.UAV.name());
		ArrayList<IData> vgui_data = sim().getObservations(Actors.VIDEO_OPERATOR_GUI.name());
<<<<<<< Upstream, based on master
		if(uav_data.contains(UAVVideoFeed.Outputs.VF_SIGNAL_OK) && vgui_data.contains(VideoGUIRole.Outputs.VGUI_NORMAL)){
=======
		if(uav_data.contains(UAVRole.Outputs.UAV_FEED_ACTIVE)
				&& vgui_data.contains(VideoGUIRole.Outputs.VGUI_NORMAL)
				&& !uav_data.contains(UAVSignal.Outputs.SIGNAL_LOST)){
>>>>>>> 7f3b80c Added the qualifier that A target sighting event cannot go active while the signal is out of range.
			return true;
		}
		return false;
	}

	@Override
	protected void activateEvent() {
		sim().addOutput(Actors.VIDEO_OPERATOR_GUI.name(), Outputs.TARGET_SIGHTED_TRUE);
	}

	@Override
	protected void finishEvent() {
		sim().addOutput(Actors.VIDEO_OPERATOR_GUI.name(), Outputs.TARGET_SIGHTED_END);

	}

	@Override
	protected int activeDuration() {
		return sim().duration(Durations.EVENT_TARGET_SIGHTED_DUR.range());
	}

}
