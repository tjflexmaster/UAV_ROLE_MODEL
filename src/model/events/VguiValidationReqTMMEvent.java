package model.events;

import model.actors.*;
import model.team.*;
import simulator.*;

public class VguiValidationReqTMMEvent extends simulator.Event {
	public VguiValidationReqTMMEvent(final ComChannelList inputs, final ComChannelList outputs)
	{
		//Define internal variables
		_name = "VguiValidationTEvent";
		
		//Define the event states
		State state = this.getState();
		_outputs = new ComChannelList();
		_outputs.putAll(outputs);
		//Define the Event transitions
		_transition = new Transition(getInternalVars(), inputs, outputs, state) {
			@SuppressWarnings("unchecked")
			@Override 
			public boolean isEnabled() {
				ComChannel<VideoOperatorGui.VIDEO_VGUI_MM_COMM> request = (ComChannel<VideoOperatorGui.VIDEO_VGUI_MM_COMM>) _inputs.get(Channels.VIDEO_VGUI_MM_COMM.name());
				if ( VideoOperatorGui.VIDEO_VGUI_MM_COMM.VGUI_ALERT_MM.equals(request.value()) ) {
					this.setTempOutput(Channels.VIDEO_VGUI_MM_COMM.name(), VideoOperatorGui.VIDEO_VGUI_MM_COMM.VGUI_VALIDATION_REQ_T_MM);
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
