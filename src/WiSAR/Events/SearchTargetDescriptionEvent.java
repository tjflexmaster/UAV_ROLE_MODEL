package WiSAR.Events;

import java.util.ArrayList;

import WiSAR.Actors;
import WiSAR.Agents.ParentSearch;
import WiSAR.Events.NewSearchAOIEvent.Outputs;
import CUAS.Simulator.Event;
import CUAS.Simulator.IData;


public class SearchTargetDescriptionEvent extends Event {

	public enum Outputs implements IData
	{
		NEW_SEARCH_TARGET_DESCRIPTION,
	}
	
	public SearchTargetDescriptionEvent(int count) {
		_count = count;
	}
	
	@Override
	protected boolean eventPossible() {
		ArrayList<IData> output = sim().getObservations(Actors.PARENT_SEARCH.name());
		if ( !output.contains(ParentSearch.Outputs.PS_TERMINATE_SEARCH) ) {
			return true;
		}
		return false;
	}

	@Override
	protected void activateEvent() {
		sim().addOutput(Actors.PARENT_SEARCH.name(), Outputs.NEW_SEARCH_TARGET_DESCRIPTION);
	}

	@Override
	protected void finishEvent() {
		sim().addOutput(Actors.PARENT_SEARCH.name(), Outputs.NEW_SEARCH_TARGET_DESCRIPTION);
	}

	@Override
	protected int activeDuration() {
		return 1;
	}

}
