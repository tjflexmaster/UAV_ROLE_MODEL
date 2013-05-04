package WiSAR.Events;

import java.util.ArrayList;

import CUAS.Simulator.Event;
import CUAS.Simulator.IData;
import WiSAR.Actors;
import WiSAR.Durations;
import WiSAR.Agents.UAVRole;
import WiSAR.submodule.UAVHeightAboveGround;

public class HAGEvent extends Event {

	public enum Outputs implements IData{
		EHAG_DANGEROUS,
		EHAG_CRASHED
	}
	
	@Override
	protected boolean eventPossible() {
		ArrayList<IData> uav = sim().getObservations(Actors.UAV.name());
		if(uav.contains(UAVRole.Outputs.UAV_FLYING) || uav.contains(UAVRole.Outputs.UAV_LOITERING)){
			return true;
		}
		return false;
	}

	@Override
	protected void activateEvent() {
		sim().addOutput(Actors.UAV.name(), Outputs.EHAG_DANGEROUS);

	}

	@Override
	protected void finishEvent() {
		ArrayList<IData> uav = sim().getObservations(Actors.UAV.name());
		//check if the UAV has adjusted it's path sufficiently or not.
		if(uav.contains(UAVHeightAboveGround.Outputs.HAG_DANGEROUS))
			sim().addOutput(Actors.UAV.name(), Outputs.EHAG_CRASHED);
	}

	@Override
	protected int activeDuration() {
		// TODO Auto-generated method stub
		return sim().duration(Durations.HAG_DANGER_TO_CRASH_DUR.range());
	}

}
