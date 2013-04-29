package WiSAR.Events;

import java.util.ArrayList;

import WiSAR.Agents.ParentSearch;
import WiSAR.Agents.Roles;
import CUAS.Simulator.Event;
import CUAS.Simulator.IData;

public class TerminateSearchEvent extends Event {

	public enum Outputs implements IData
	{
		TERMINATE_SEARCH,
		TERMINATE_SEARCH_END
	}
	
	public TerminateSearchEvent() {
		_count = 1;
	}
	
	@Override
	protected boolean eventPossible() {
		ArrayList<IData> output = sim().getObservations(Roles.PARENT_SEARCH.name());
		if ( !output.contains(ParentSearch.Outputs.SEARCH_TERMINATED) &&
				!output.contains(ParentSearch.Outputs.PS_BUSY) ) {
			return true;
		}
		return false;
	}

	@Override
	protected void activateEvent() {
		sim().addOutput(Roles.PARENT_SEARCH.name(), Outputs.TERMINATE_SEARCH);
	}

	@Override
	protected void finishEvent() {
		sim().addOutput(Roles.PARENT_SEARCH.name(), Outputs.TERMINATE_SEARCH_END);

	}

	@Override
	protected int activeDuration() {
		return 1;
	}

}
