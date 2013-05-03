package WiSAR.Events;

import java.util.ArrayList;

import CUAS.Simulator.Event;
import CUAS.Simulator.IData;
import WiSAR.Actors;
import WiSAR.Durations;
import WiSAR.Agents.UAVRole;
import WiSAR.Agents.VideoGUIRole;

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
		if(uav_data.contains(UAVRole.Outputs.UAV_FEED_ACTIVE) && vgui_data.contains(VideoGUIRole.Outputs.VGUI_NORMAL)){
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
