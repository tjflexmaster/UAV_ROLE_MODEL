package model.events;


import model.team.Channels;

import simulator.ComChannel;
import simulator.ComChannelList;
import simulator.Event;
import simulator.ITransition;
import simulator.State;
import simulator.Transition;

public class NewSearchEvent extends Event {
	
	public NewSearchEvent(final ComChannelList inputs, final ComChannelList outputs)
	{
		//Define internal variables
		_name = "NewSearchEvent";
		
		//Define the event states
		State state = this.getState();
		
		//Define the Event transitions
		_transition = new Transition(getInternalVars(), inputs, outputs, state) {
			@SuppressWarnings("unchecked")
			@Override 
			public boolean isEnabled() {
				
				//TODO Check the simulator to see if we have a transition already
				ComChannel<Boolean> new_search_event = (ComChannel<Boolean>) inputs.get(Channels.NEW_SEARCH_EVENT.name());
				if ( !new_search_event.value() ) {
					this.setTempOutput(Channels.NEW_SEARCH_EVENT.name(), true);
					return true;
				} else
					return false;
			};
		};
		state.add(_transition);
		
	}

	@Override
	public ITransition getEnabledTransition() {
		return _transition;
	}

}
