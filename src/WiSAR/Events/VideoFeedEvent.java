package WiSAR.Events;

import java.util.ArrayList;

import CUAS.Simulator.Event;
import CUAS.Simulator.IData;
import WiSAR.Actors;
import WiSAR.Agents.UAVRole;
import WiSAR.submodule.UAVVideoFeed;

public class VideoFeedEvent extends Event {

	public enum Outputs implements IData{
		E_VSIGNAL_BAD,
	}
	@Override
	protected boolean eventPossible() {
		ArrayList<IData> uav = sim().getObservations(Actors.UAV.name());
		if(uav.contains(UAVVideoFeed.Outputs.VF_SIGNAL_OK)){
			return true;
		}
		return false;
	}

	@Override
	protected void activateEvent() {
		sim().addOutput(Actors.UAV.name(), Outputs.E_VSIGNAL_BAD);

	}

	@Override
	protected void finishEvent() {

	}

	@Override
	protected int activeDuration() {
		return 1;
	}

}
