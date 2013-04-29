package WiSAR.Events;

import java.util.ArrayList;

import org.omg.PortableInterceptor.INACTIVE;

import WiSAR.Agents.ParentSearch;
import WiSAR.Agents.Roles;
import CUAS.Simulator.Event;
import CUAS.Simulator.IData;

public class NewSearchAOIEvent extends Event {

	public enum Outputs implements IData
	{
		NEW_SEARCH_AOI,
		NEW_SEARCH_AOI_END
	}
	
	public NewSearchAOIEvent(int count) {
		_count = count;
	}
	
	@Override
	protected boolean eventPossible() {
		ArrayList<IData> output = sim().getObservations(Roles.PARENT_SEARCH.name());
		if ( !output.contains(ParentSearch.Outputs.SEARCH_TERMINATED) ) {
			return true;
		}
		return false;
	}

	@Override
	protected void activateEvent() {
		sim().addOutput(Roles.PARENT_SEARCH.name(), Outputs.NEW_SEARCH_AOI);
	}

	@Override
	protected void finishEvent() {
		sim().addOutput(Roles.PARENT_SEARCH.name(), Outputs.NEW_SEARCH_AOI_END);

	}

	@Override
	protected int activeDuration() {
		return 1;
	}

}
