package model.events;

import model.actors.*;
import model.actors.VideoOperatorGui.VIDEO_VGUI_MM_COMM;
import model.team.*;
import simulator.*;

public class VguiAlertMMEvent extends simulator.Event {
	public VguiAlertMMEvent(final ComChannelList inputs, final ComChannelList outputs)
	{
		//Define internal variables
		_name = "VguiAlertEvent";
		
		//Define the event states
		State state = this.getState();
		_outputs = new ComChannelList();
		_outputs.putAll(outputs);
		//Define the Event transitions
		_transition = new Transition(getInternalVars(), inputs, outputs, state, Duration.VO_TX_MM.getRange()) {
			@SuppressWarnings("unchecked")
			@Override 
			public boolean isEnabled() {
				ComChannel<VIDEO_VGUI_MM_COMM> alert_event = (ComChannel<VIDEO_VGUI_MM_COMM>) _inputs.get(Channels.VIDEO_VGUI_MM_COMM.name());
				if ( !VideoOperatorGui.VIDEO_VGUI_MM_COMM.VGUI_ALERT_MM.equals(alert_event.value()) ) {
					this.setTempOutput(Channels.VIDEO_VGUI_MM_COMM.name(), VideoOperatorGui.VIDEO_VGUI_MM_COMM.VGUI_ALERT_MM);
					return true;
				} else
					return false;
			};
		};
		state.add(_transition);
		
	}

	@Override
	public ITransition getEnabledTransition() {
		if(_transition.isEnabled())
			return _transition;
		return null;
	}

	@Override
	public IState getCurrentState() {
		// TODO Auto-generated method stub
		return null;
	}
}
