package WiSAR.Events;

import java.util.ArrayList;

import CUAS.Simulator.Event;
import CUAS.Simulator.IData;
import WiSAR.Actors;
import WiSAR.Durations;
import WiSAR.Agents.UAVRole;
import WiSAR.submodule.UAVSignal;

public class SignalEvent extends Event {

	public enum Outputs implements IData{
		E_SIGNAL_LOST,
		E_SIGNAL_BACK
	}
	
	/**
	 * Assumption that flybys occur in areas that we've already been
	 *  and that a signal will only be lost while flying to new locations.
	 *  Also the signal can only be lost if it is currently active.
	 */
	@Override
	protected boolean eventPossible() {
		ArrayList<IData> uav = sim().getObservations(Actors.UAV.name());
		if(uav.contains(UAVRole.Outputs.UAV_FLYING_NORMAL)
				&& uav.contains(UAVSignal.Outputs.SIGNAL_OK)){
			return true;
		}
		return false;
	}

	@Override
	protected void activateEvent() {
		sim().addOutput(Actors.UAV.name(), Outputs.E_SIGNAL_LOST);
	}

	@Override
	protected void finishEvent() {
		sim().addOutput(Actors.UAV.name(), Outputs.E_SIGNAL_BACK);

	}

	@Override
	protected int activeDuration() {
		return sim().duration(Durations.EVENT_SIGNAL_LOST_DUR.range());
	}

}
