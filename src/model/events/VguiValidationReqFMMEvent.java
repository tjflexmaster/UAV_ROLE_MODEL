package model.events;

import model.actors.*;
import model.team.*;
import simulator.*;

public class VguiValidationReqFMMEvent extends simulator.Event {
	public VguiValidationReqFMMEvent(final ComChannelList inputs, final ComChannelList outputs)
	{
		//Define internal variables
		_name = "VguiValidationFEvent";
		
		//Define the event states
		State state = this.getState();
		_outputs = new ComChannelList();
		_outputs.putAll(outputs);
		//Define the Event transitions
		_transition = new Transition(getInternalVars(), inputs, outputs, state, Duration.VO_TX_MM.getRange()) { 
			@Override 
			public boolean isEnabled() {
				this.setTempOutput(Channels.VIDEO_VGUI_MM_COMM.name(), VideoOperatorGui.VIDEO_VGUI_MM_COMM.VGUI_VALIDATION_REQ_F_MM);
				return true;
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
